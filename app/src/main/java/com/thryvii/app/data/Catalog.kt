package com.thryvii.app.data

import com.thryvii.app.data.model.CatalogItem
import com.thryvii.app.data.model.ItemKind
import com.thryvii.app.data.model.MysteryBag

/** The fixed list of things the shop sells. Every item currently costs 10 coins. */
object Catalog {

    val plants: List<CatalogItem> = listOf(
        "sunflower" to "Sunflower", "tulip" to "Tulip", "lavender" to "Lavender",
        "rose" to "Rose", "marigold" to "Marigold", "hibiscus" to "Hibiscus",
        "daisy" to "Daisy", "pumpkin" to "Pumpkin", "tomato" to "Tomato",
        "apple" to "Apple", "strawberry" to "Strawberry", "watermelon" to "Watermelon",
        "orange" to "Orange", "banana" to "Banana", "corn" to "Corn",
        "broccoli" to "Broccoli", "lettuce" to "Lettuce", "mushroom" to "Mushroom",
        "carrot" to "Carrot", "blueberry" to "Blueberry", "grape" to "Grape"
    ).map { (name, label) -> CatalogItem("plant_$name", label, ItemKind.PLANT) }

    val decorations: List<CatalogItem> = listOf(
        "fence" to "Fence", "scarecrow" to "Scarecrow", "fountain" to "Fountain",
        "rose_arch" to "Rose Arch", "watering_can" to "Watering Can",
        "square_pot" to "Square Pot", "shovel" to "Shovel", "bench" to "Bench",
        "rocks" to "Rocks", "paved_path" to "Paved Path", "round_pot" to "Round Pot",
        "gnome" to "Gnome", "stone_path" to "Stone Path", "bird_bath" to "Bird Bath",
        "wishing_well" to "Wishing Well", "light" to "Light", "picnic_table" to "Picnic Table"
    ).map { (name, label) -> CatalogItem("decor_$name", label, ItemKind.DECORATION) }

    val bags: List<MysteryBag> = listOf(
        MysteryBag(
            "bag_flower", "Flower Bag",
            listOf("sunflower", "tulip", "lavender", "rose", "marigold", "hibiscus", "daisy")
                .map { "plant_$it" }
        ),
        MysteryBag(
            "bag_vegetable", "Vegetable Bag",
            listOf("pumpkin", "tomato", "corn", "broccoli", "lettuce", "mushroom", "carrot")
                .map { "plant_$it" }
        ),
        MysteryBag(
            "bag_fruit", "Fruit Bag",
            listOf("apple", "strawberry", "watermelon", "orange", "banana", "blueberry", "grape")
                .map { "plant_$it" }
        ),
        MysteryBag("bag_decoration", "Decoration Bag", decorations.map { it.key })
    )

    private val itemsByKey: Map<String, CatalogItem> =
        (plants + decorations).associateBy { it.key }

    fun item(key: String): CatalogItem? = itemsByKey[key]

    fun bag(key: String): MysteryBag? = bags.firstOrNull { it.key == key }
}
