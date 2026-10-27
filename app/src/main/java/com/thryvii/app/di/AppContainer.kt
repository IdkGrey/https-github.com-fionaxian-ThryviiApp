package com.thryvii.app.di

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.thryvii.app.data.repository.DefaultGardenRepository
import com.thryvii.app.data.repository.DefaultJournalRepository
import com.thryvii.app.data.repository.DefaultQuestRepository
import com.thryvii.app.data.repository.DefaultSessionRepository
import com.thryvii.app.data.repository.DefaultWalletRepository
import com.thryvii.app.data.repository.GardenRepository
import com.thryvii.app.data.repository.JournalRepository
import com.thryvii.app.data.repository.QuestRepository
import com.thryvii.app.data.repository.SessionRepository
import com.thryvii.app.data.repository.WalletRepository
import com.thryvii.app.data.state.AppState
import com.thryvii.app.data.state.AppStateSerializer
import com.thryvii.app.data.state.AppStateStore
import com.thryvii.app.data.state.DataStoreStateStore
import com.thryvii.app.data.state.InMemoryStateStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Builds the repositories once for the whole process. This is the single place that decides
 * where data lives: set [persistToDisk] to false to keep everything in memory only (data is
 * then lost when the app is closed), which is handy for demos and tests.
 */
class AppContainer(context: Context, persistToDisk: Boolean = true) {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val store: AppStateStore =
        if (persistToDisk) {
            DataStoreStateStore(
                dataStore = DataStoreFactory.create(
                    serializer = AppStateSerializer,
                    corruptionHandler = ReplaceFileCorruptionHandler { AppState() },
                    scope = appScope,
                    produceFile = { context.dataStoreFile("thryvii_state.json") }
                ),
                scope = appScope
            )
        } else {
            InMemoryStateStore()
        }

    val session: SessionRepository = DefaultSessionRepository(store)
    val wallet: WalletRepository = DefaultWalletRepository(store)
    val garden: GardenRepository = DefaultGardenRepository(store)
    val journal: JournalRepository = DefaultJournalRepository(store)
    val quests: QuestRepository = DefaultQuestRepository(store)
}
