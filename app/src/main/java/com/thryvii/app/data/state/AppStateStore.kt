package com.thryvii.app.data.state

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

/**
 * Where the [AppState] lives. The repositories only talk to this interface, so "in memory only"
 * and "saved to disk" are one swap in AppContainer.
 */
interface AppStateStore {
    val state: StateFlow<AppState>

    /** False until the saved state (if any) has been read. Always true for the in-memory store. */
    val loaded: StateFlow<Boolean>

    /**
     * Atomically turns the current state into a new one and returns a result computed from it.
     * Calls are serialized, so "check coins, then spend them" cannot interleave with another call.
     */
    suspend fun <T> transact(block: (AppState) -> Pair<AppState, T>): T
}

suspend fun AppStateStore.update(block: (AppState) -> AppState) {
    transact { current -> block(current) to Unit }
}

/** Data lives only as long as the app process. */
class InMemoryStateStore(initial: AppState = AppState()) : AppStateStore {
    private val mutableState = MutableStateFlow(initial)
    private val mutex = Mutex()

    override val state: StateFlow<AppState> = mutableState.asStateFlow()
    override val loaded: StateFlow<Boolean> = MutableStateFlow(true).asStateFlow()

    override suspend fun <T> transact(block: (AppState) -> Pair<AppState, T>): T =
        mutex.withLock {
            val (next, result) = block(mutableState.value)
            mutableState.value = next
            result
        }
}

/** Data is saved to a JSON file by Jetpack DataStore, so it survives app restarts. */
class DataStoreStateStore(
    private val dataStore: DataStore<AppState>,
    scope: CoroutineScope
) : AppStateStore {
    private val mutableState = MutableStateFlow(AppState())
    private val mutableLoaded = MutableStateFlow(false)

    override val state: StateFlow<AppState> = mutableState.asStateFlow()
    override val loaded: StateFlow<Boolean> = mutableLoaded.asStateFlow()

    init {
        scope.launch {
            dataStore.data
                .catch { emit(AppState()) }
                .collect { saved ->
                    // State first, then the flag: anyone who sees loaded == true is guaranteed
                    // to read the saved state, not the placeholder default.
                    mutableState.value = saved
                    mutableLoaded.value = true
                }
        }
    }

    override suspend fun <T> transact(block: (AppState) -> Pair<AppState, T>): T {
        var result: Any? = null
        dataStore.updateData { current ->
            val (next, value) = block(current)
            result = value
            next
        }
        @Suppress("UNCHECKED_CAST")
        return result as T
    }
}

/** Reads and writes [AppState] as JSON. Unknown or missing fields are tolerated. */
object AppStateSerializer : Serializer<AppState> {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        coerceInputValues = true
    }

    override val defaultValue: AppState = AppState()

    override suspend fun readFrom(input: InputStream): AppState =
        try {
            json.decodeFromString(AppState.serializer(), input.readBytes().decodeToString())
        } catch (e: SerializationException) {
            throw CorruptionException("Cannot read saved app state", e)
        }

    override suspend fun writeTo(t: AppState, output: OutputStream) {
        output.write(json.encodeToString(AppState.serializer(), t).encodeToByteArray())
    }
}
