package com.thryvii.app.data.state

import com.thryvii.app.data.model.JournalEntry
import com.thryvii.app.data.model.PlacedItem
import com.thryvii.app.data.model.Profile
import com.thryvii.app.data.model.Quest
import kotlinx.serialization.Serializable

/**
 * Everything the app remembers, in one serializable object. A fresh install starts from the
 * defaults below (the seed data). Every repository reads and writes slices of this, so it is
 * also exactly what gets saved to disk by [DataStoreStateStore].
 *
 * [version] is for future migrations: bump it and convert old data when the shape changes.
 */
@Serializable
data class AppState(
    val version: Int = CURRENT_VERSION,
    val profile: Profile? = null,
    val coins: Int = SeedData.STARTING_COINS,
    val inventory: Map<String, Int> = SeedData.inventory,
    val placed: List<PlacedItem> = emptyList(),
    val nextPlacedId: Long = 1L,
    val journal: List<JournalEntry> = SeedData.journal,
    val quests: List<Quest> = SeedData.quests
) {
    companion object {
        const val CURRENT_VERSION = 1
    }
}
