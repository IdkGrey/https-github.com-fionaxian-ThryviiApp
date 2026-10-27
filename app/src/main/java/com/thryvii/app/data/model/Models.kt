package com.thryvii.app.data.model

import kotlinx.serialization.Serializable

// Plain, serializable domain models. They hold only strings, numbers and lists (no Compose
// types and no R.drawable ids), so the exact same objects can live in memory or be written to
// disk. Artwork is referenced by a stable string key (e.g. "plant_rose"); the UI turns keys
// back into drawables via ui/ResourceKeys.kt.

@Serializable
data class Profile(
    val email: String = "",
    val username: String = "",
    val gardenName: String = "",
    val trackLocation: Boolean = false,
    val starterSeed: String = "",
    val hairstyleIndex: Int = 2,
    val colorIndex: Int = 1,
    val onboardingComplete: Boolean = false
)

enum class ItemKind { PLANT, DECORATION }

/** One purchasable thing. [key] doubles as the drawable name, e.g. "plant_rose". */
data class CatalogItem(
    val key: String,
    val label: String,
    val kind: ItemKind,
    val price: Int = 10
)

/** A mystery bag: buying it gives one random item whose key is in [pool]. */
data class MysteryBag(
    val key: String,
    val label: String,
    val pool: List<String>,
    val price: Int = 10
)

/** An item the player has put into the garden (see GardenRepository.place). */
@Serializable
data class PlacedItem(val id: Long, val key: String)

sealed interface PurchaseResult {
    data class Success(val received: CatalogItem, val price: Int) : PurchaseResult
    object NotEnoughCoins : PurchaseResult
    object UnknownItem : PurchaseResult
}

enum class EntryType { DRAWING, WRITING }

@Serializable
data class StrokePoint(val x: Float, val y: Float)

/** A freehand stroke with Compose types flattened to plain numbers (ARGB int, width in dp). */
@Serializable
data class StrokeData(
    val points: List<StrokePoint>,
    val colorArgb: Int,
    val widthDp: Float
)

@Serializable
data class JournalEntry(
    val title: String,
    val caption: String,
    val pageNumber: Int,
    val preview: String?,
    val type: EntryType,
    val date: String,
    val prompt: String,
    val fullBody: String,
    val photoKey: String? = null,
    val artKey: String? = null,
    val viewerReady: Boolean = true,
    val strokes: List<StrokeData>? = null
)

enum class QuestStatus { NOT_STARTED, IN_PROGRESS, COMPLETED }

enum class QuestGroup { WEATHER, DAILY }

@Serializable
data class Quest(
    val id: String,
    val iconKey: String,
    val text: String,
    val group: QuestGroup,
    val status: QuestStatus,
    val progressText: String? = null,
    val rewardCoins: Int = 5
)
