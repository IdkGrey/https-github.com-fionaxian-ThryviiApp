package com.thryvii.app.data.state

import com.thryvii.app.data.model.EntryType
import com.thryvii.app.data.model.JournalEntry
import com.thryvii.app.data.model.Quest
import com.thryvii.app.data.model.QuestGroup
import com.thryvii.app.data.model.QuestStatus

/** The starting content a brand-new install begins with (previously hard-coded in MainScreen.kt). */
object SeedData {

    const val STARTING_COINS = 10

    val inventory: Map<String, Int> = mapOf(
        "decor_fence" to 2,
        "decor_watering_can" to 1,
        "decor_shovel" to 1,
        "decor_paved_path" to 5,
        "plant_sunflower" to 1,
        "plant_tulip" to 1,
        "plant_daisy" to 1,
        "plant_carrot" to 2
    )

    val journal: List<JournalEntry> = listOf(
        JournalEntry(
            title = "Fox!",
            caption = "Yesterday, art",
            pageNumber = 4,
            preview = null,
            type = EntryType.DRAWING,
            date = "10/3/25",
            prompt = "Today's Prompt: Draw your favorite animal!",
            fullBody = "",
            artKey = "entry_drawing_fox"
        ),
        JournalEntry(
            title = "My Parents",
            caption = "Yesterday, writing",
            pageNumber = 3,
            preview = "I'm really grateful for...",
            type = EntryType.WRITING,
            date = "10/3/25",
            prompt = "Today's Prompt: What are you most grateful for?",
            fullBody = "I'm really grateful for my parents today. They've always worked so hard to " +
                "give me opportunities, even when they never had the same ones themselves. I " +
                "sometimes take that for granted, but lately I've been noticing all the quiet " +
                "things they do — like leaving food for me when they know I'm studying late, or " +
                "pretending not to worry even when I can tell they're exhausted. It makes me " +
                "realize how much love can be shown in small, unspoken ways. I'm also thankful " +
                "for how our relationship has grown. We still argue sometimes, but I think we " +
                "understand each other more now than before. I'm learning to see them not just " +
                "as parents, but as people with their own worries and dreams. That thought alone " +
                "makes me want to try harder — not because they expect it, but because I want to " +
                "make them proud.",
            photoKey = "entry_photo_my_parents"
        ),
        JournalEntry(
            title = "Anger",
            caption = "Yesterday, writing",
            pageNumber = 2,
            preview = "I feel frustrated to...",
            type = EntryType.WRITING,
            date = "10/1/25",
            prompt = "Today's Prompt: What made you feel frustrated today?",
            fullBody = "I feel frustrated to...",
            viewerReady = false
        ),
        JournalEntry(
            title = "First Journal",
            caption = "Yesterday, writing",
            pageNumber = 1,
            preview = "I've never used this....",
            type = EntryType.WRITING,
            date = "9/28/25",
            prompt = "Today's Prompt: Write about how you're feeling right now.",
            fullBody = "I've never used this....",
            viewerReady = false
        )
    )

    // Quest ids are referenced from the UI (e.g. reading the quote completes DAILY_QUOTE).
    const val QUEST_WEATHER_DRAW = "weather_draw"
    const val QUEST_WEATHER_MEMORY = "weather_memory"
    const val QUEST_DAILY_WALK_SHORT = "daily_walk_short"
    const val QUEST_DAILY_WALK_MILE = "daily_walk_mile"
    const val QUEST_DAILY_HEART = "daily_heart"
    const val QUEST_DAILY_QUOTE = "daily_quote"
    const val QUEST_DAILY_JOURNAL = "daily_journal"

    val quests: List<Quest> = listOf(
        Quest(QUEST_WEATHER_DRAW, "icon_weather", "Draw the weather in your journal",
            QuestGroup.WEATHER, QuestStatus.NOT_STARTED),
        Quest(QUEST_WEATHER_MEMORY, "quest_book", "Journal your favorite memory",
            QuestGroup.WEATHER, QuestStatus.NOT_STARTED),
        Quest(QUEST_DAILY_WALK_SHORT, "quest_walk", "Walk for 10 minutes",
            QuestGroup.DAILY, QuestStatus.COMPLETED, "100%"),
        Quest(QUEST_DAILY_WALK_MILE, "quest_walk", "Walk for 1 mile",
            QuestGroup.DAILY, QuestStatus.IN_PROGRESS, "38%"),
        Quest(QUEST_DAILY_HEART, "notif_smiley", "Send a heart to another virtual garden",
            QuestGroup.DAILY, QuestStatus.NOT_STARTED),
        Quest(QUEST_DAILY_QUOTE, "quest_quote", "Read the inspirational quote of the day",
            QuestGroup.DAILY, QuestStatus.NOT_STARTED),
        Quest(QUEST_DAILY_JOURNAL, "quest_book", "Journal your thoughts and feelings",
            QuestGroup.DAILY, QuestStatus.NOT_STARTED)
    )
}
