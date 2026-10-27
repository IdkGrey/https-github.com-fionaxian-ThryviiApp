package com.thryvii.app.data.repository

import com.thryvii.app.data.model.JournalEntry
import com.thryvii.app.data.model.PlacedItem
import com.thryvii.app.data.model.Profile
import com.thryvii.app.data.model.PurchaseResult
import com.thryvii.app.data.model.Quest
import kotlinx.coroutines.flow.Flow

/** Who is signed in and how far through onboarding they are. */
data class SessionState(val loaded: Boolean, val profile: Profile?)

interface SessionRepository {
    val session: Flow<SessionState>

    /** Mock auth: any email is accepted. Creates the profile if there isn't one yet. */
    suspend fun signUp(email: String)

    /** Mock auth: any username/email and password are accepted. */
    suspend fun signIn(usernameOrEmail: String)

    suspend fun saveGarden(gardenName: String, username: String, trackLocation: Boolean)
    suspend fun saveStarterSeed(seed: String)

    /** Saves the avatar and marks onboarding as finished. */
    suspend fun saveAvatar(hairstyleIndex: Int, colorIndex: Int)

    suspend fun signOut()
}

interface WalletRepository {
    val coins: Flow<Int>
    suspend fun add(amount: Int)
}

interface GardenRepository {
    /** Item key (e.g. "plant_rose") to how many the player owns. */
    val inventory: Flow<Map<String, Int>>
    val placed: Flow<List<PlacedItem>>

    /**
     * Buys a shop item or mystery bag by catalog key. Debits the wallet and adds to the
     * inventory in one atomic step, so a failed purchase never costs coins or gives an item.
     */
    suspend fun purchase(catalogKey: String): PurchaseResult

    /** Moves one owned item from the inventory into the garden. False if none are owned. */
    suspend fun place(itemKey: String): Boolean
}

interface JournalRepository {
    /** Newest first. */
    val entries: Flow<List<JournalEntry>>

    /** Adds the entry on top and assigns the next page number. */
    suspend fun add(entry: JournalEntry)
}

interface QuestRepository {
    val quests: Flow<List<Quest>>

    /** Marks the quest complete and pays its coin reward. False if unknown or already done. */
    suspend fun complete(questId: String): Boolean
}
