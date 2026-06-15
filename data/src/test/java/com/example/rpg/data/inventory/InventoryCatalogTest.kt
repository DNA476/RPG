package com.example.rpg.data.inventory

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InventoryCatalogTest {
    @Test
    fun catalogHasUniqueIdsAndEveryEquipmentSlot() {
        assertEquals(
            InventoryCatalog.items.size,
            InventoryCatalog.items.map(InventoryItem::id).distinct().size,
        )
        EquipmentSlot.entries.forEach { slot ->
            assertTrue(InventoryCatalog.items.any { it.slot == slot })
        }
    }

    @Test
    fun legendaryItemsAreQuestExclusiveAndNotDemoOwned() {
        val legendaryItems = InventoryCatalog.items.filter {
            it.rarity == ItemRarity.LEGENDARY
        }

        assertFalse(legendaryItems.isEmpty())
        assertTrue(legendaryItems.all(InventoryItem::questExclusive))
        assertTrue(legendaryItems.none { it.id in InventoryCatalog.demoOwnedItemIds })
    }
}
