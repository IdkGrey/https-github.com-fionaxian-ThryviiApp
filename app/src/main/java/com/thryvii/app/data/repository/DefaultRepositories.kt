package com.thryvii.app.data.repository

import com.thryvii.app.data.Catalog
import com.thryvii.app.data.model.JournalEntry
import com.thryvii.app.data.model.PlacedItem
import com.thryvii.app.data.model.Profile
import com.thryvii.app.data.model.PurchaseResult
import com.thryvii.app.data.model.Quest
import com.thryvii.app.data.model.QuestStatus
import com.thryvii.app.data.state.AppStateStore
import com.thryvii.app.data.state.update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.random.Random

// All five repositories are thin views over one AppStateStore. They hold no data themselves, so
// whether it is kept in memory or saved to disk is decided by which store AppContainer builds.

class DefaultSessionRepository(private val store: AppStateStore) : SessionRepository {

    override val session: Flow<SessionState> =
        combine(store.loaded, store.state) { loaded, state -> SessionState(loaded, state.profile) }
            .distinctUntilChanged()

    override suspend fun signUp(email: String) = store.update { s ->
        s.copy(profile = (s.profile ?: Profile()).copy(email = email.trim()))
    }

    override suspend fun signIn(usernameOrEmail: String) = store.update { s ->
        s.copy(profile = s.profile ?: Profile(email = usernameOrEmail.trim()))
    }

    override suspend fun saveGarden(gardenName: String, username: String, trackLocation: Boolean) =
        store.update { s ->
            s.copy(
                profile = (s.profile ?: Profile()).copy(
                    gardenName = gardenName.trim(),
                    username = username.trim(),
                    trackLocation = trackLocation
                )
            )
        }

    override suspend fun saveStarterSeed(seed: String) = store.update { s ->
        s.copy(profile = (s.profile ?: Profile()).copy(starterSeed = seed))
    }

    override suspend fun saveAvatar(hairstyleIndex: Int, colorIndex: Int) = store.update { s ->
        s.copy(
            profile = (s.profile ?: Profile()).copy(
                hairstyleIndex = hairstyleIndex,
                colorIndex = colorIndex,
                onboardingComplete = true
            )
        )
    }

    override suspend fun signOut() = store.update { s -> s.copy(profile = null) }
}

class DefaultWalletRepository(private val store: AppStateStore) : WalletRepository {

    override val coins: Flow<Int> = store.state.map { it.coins }.distinctUntilChanged()

    override suspend fun add(amount: Int) = store.update { s ->
        s.copy(coins = (s.coins + amount).coerceAtLeast(0))
    }
}

class DefaultGardenRepository(private val store: AppStateStore) : GardenRepository {

    override val inventory: Flow<Map<String, Int>> =
        store.state.map { it.inventory }.distinctUntilChanged()

    override val placed: Flow<List<PlacedItem>> =
        store.state.map { it.placed }.distinctUntilChanged()

    override suspend fun purchase(catalogKey: String): PurchaseResult =
        store.transact { s ->
            val bag = Catalog.bag(catalogKey)
            val received = if (bag != null) Catalog.item(bag.pool.random(Random.Default)) else Catalog.item(catalogKey)
            val price = bag?.price ?: received?.price
            if (received == null || price == null) {
                return@transact s to PurchaseResult.UnknownItem
            }
            if (s.coins < price) {
                return@transact s to PurchaseResult.NotEnoughCoins
            }
            val owned = s.inventory[received.key] ?: 0
            val next = s.copy(
                coins = s.coins - price,
                inventory = s.inventory + (received.key to owned + 1)
            )
            next to PurchaseResult.Success(received, price)
        }

    override suspend fun place(itemKey: String): Boolean =
        store.transact { s ->
            val owned = s.inventory[itemKey] ?: 0
            if (owned <= 0) {
                s to false
            } else {
                s.copy(
                    inventory = s.inventory + (itemKey to owned - 1),
                    placed = s.placed + PlacedItem(s.nextPlacedId, itemKey),
                    nextPlacedId = s.nextPlacedId + 1
                ) to true
            }
        }
}

class DefaultJournalRepository(private val store: AppStateStore) : JournalRepository {

    override val entries: Flow<List<JournalEntry>> =
        store.state.map { it.journal }.distinctUntilChanged()

    override suspend fun add(entry: JournalEntry) = store.update { s ->
        val nextPage = (s.journal.maxOfOrNull { it.pageNumber } ?: 0) + 1
        s.copy(journal = listOf(entry.copy(pageNumber = nextPage)) + s.journal)
    }
}

class DefaultQuestRepository(private val store: AppStateStore) : QuestRepository {

    override val quests: Flow<List<Quest>> =
        store.state.map { it.quests }.distinctUntilChanged()

    override suspend fun complete(questId: String): Boolean =
        store.transact { s ->
            val quest = s.quests.firstOrNull { it.id == questId }
            if (quest == null || quest.status == QuestStatus.COMPLETED) {
                s to false
            } else {
                s.copy(
                    coins = s.coins + quest.rewardCoins,
                    quests = s.quests.map {
                        if (it.id == questId) it.copy(status = QuestStatus.COMPLETED, progressText = "100%") else it
                    }
                ) to true
            }
        }
}
