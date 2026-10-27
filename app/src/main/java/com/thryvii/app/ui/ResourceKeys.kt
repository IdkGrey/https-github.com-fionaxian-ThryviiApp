package com.thryvii.app.ui

import com.thryvii.app.R

/**
 * Saved data refers to artwork by a stable string key (e.g. "plant_rose") instead of by
 * R.drawable.plant_rose, because drawable ids are numbers generated at build time and can
 * change between builds. This table turns a saved key back into the current drawable id.
 *
 * When you add artwork that the data layer refers to, add a line here.
 */
private val drawableKeys: Map<String, Int> = mapOf(
    "plant_sunflower" to R.drawable.plant_sunflower,
    "plant_tulip" to R.drawable.plant_tulip,
    "plant_lavender" to R.drawable.plant_lavender,
    "plant_rose" to R.drawable.plant_rose,
    "plant_marigold" to R.drawable.plant_marigold,
    "plant_hibiscus" to R.drawable.plant_hibiscus,
    "plant_daisy" to R.drawable.plant_daisy,
    "plant_pumpkin" to R.drawable.plant_pumpkin,
    "plant_tomato" to R.drawable.plant_tomato,
    "plant_apple" to R.drawable.plant_apple,
    "plant_strawberry" to R.drawable.plant_strawberry,
    "plant_watermelon" to R.drawable.plant_watermelon,
    "plant_orange" to R.drawable.plant_orange,
    "plant_banana" to R.drawable.plant_banana,
    "plant_corn" to R.drawable.plant_corn,
    "plant_broccoli" to R.drawable.plant_broccoli,
    "plant_lettuce" to R.drawable.plant_lettuce,
    "plant_mushroom" to R.drawable.plant_mushroom,
    "plant_carrot" to R.drawable.plant_carrot,
    "plant_blueberry" to R.drawable.plant_blueberry,
    "plant_grape" to R.drawable.plant_grape,
    "decor_fence" to R.drawable.decor_fence,
    "decor_scarecrow" to R.drawable.decor_scarecrow,
    "decor_fountain" to R.drawable.decor_fountain,
    "decor_rose_arch" to R.drawable.decor_rose_arch,
    "decor_watering_can" to R.drawable.decor_watering_can,
    "decor_square_pot" to R.drawable.decor_square_pot,
    "decor_shovel" to R.drawable.decor_shovel,
    "decor_bench" to R.drawable.decor_bench,
    "decor_rocks" to R.drawable.decor_rocks,
    "decor_paved_path" to R.drawable.decor_paved_path,
    "decor_round_pot" to R.drawable.decor_round_pot,
    "decor_gnome" to R.drawable.decor_gnome,
    "decor_stone_path" to R.drawable.decor_stone_path,
    "decor_bird_bath" to R.drawable.decor_bird_bath,
    "decor_wishing_well" to R.drawable.decor_wishing_well,
    "decor_light" to R.drawable.decor_light,
    "decor_picnic_table" to R.drawable.decor_picnic_table,
    "bag_flower" to R.drawable.bag_flower,
    "bag_vegetable" to R.drawable.bag_vegetable,
    "bag_fruit" to R.drawable.bag_fruit,
    "bag_decoration" to R.drawable.bag_decoration,
    "entry_drawing_fox" to R.drawable.entry_drawing_fox,
    "entry_photo_my_parents" to R.drawable.entry_photo_my_parents,
    "icon_weather" to R.drawable.icon_weather,
    "quest_book" to R.drawable.quest_book,
    "quest_walk" to R.drawable.quest_walk,
    "notif_smiley" to R.drawable.notif_smiley,
    "quest_quote" to R.drawable.quest_quote,
    "icon_coin" to R.drawable.icon_coin
)

/** Drawable id for a saved key. Unknown keys fall back to the coin icon instead of crashing. */
fun drawableFor(key: String?): Int = drawableKeys[key] ?: R.drawable.icon_coin

private val keysByDrawable: Map<Int, String> = drawableKeys.entries.associate { (key, res) -> res to key }

/** The saved key for a drawable id, or null if that drawable isn't in the table above. */
fun keyFor(drawableRes: Int?): String? = drawableRes?.let { keysByDrawable[it] }
