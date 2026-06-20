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
    fun legendaryItemsAreQuestExclusiveAndNotStarterOwned() {
        val legendaryItems = InventoryCatalog.items.filter {
            it.rarity == ItemRarity.LEGENDARY
        }

        assertFalse(legendaryItems.isEmpty())
        assertTrue(legendaryItems.all(InventoryItem::questExclusive))
        assertTrue(legendaryItems.none { it.id in InventoryCatalog.starterItemIds })
    }

    @Test
    fun starterLoadoutCoversEveryNonArtifactSlotWithoutQuestRewards() {
        val starterItems = InventoryCatalog.items.filter {
            it.id in InventoryCatalog.starterItemIds
        }

        assertEquals(6, starterItems.size)
        assertTrue(starterItems.none { it.slot == EquipmentSlot.ARTIFACT })
        assertTrue(starterItems.none(InventoryItem::questExclusive))
        EquipmentSlot.entries.filterNot { it == EquipmentSlot.ARTIFACT }.forEach { slot ->
            assertEquals(1, starterItems.count { it.slot == slot })
        }
    }

    @Test
    fun regularVictoriesUnlockTheNonStarterEquipmentPoolInOrder() {
        val rewardItems = InventoryCatalog.victoryEquipmentItemIds.mapNotNull(InventoryCatalog::get)

        assertEquals(14, rewardItems.size)
        assertTrue(rewardItems.none { it.id in InventoryCatalog.starterItemIds })
        assertTrue(rewardItems.none(InventoryItem::questExclusive))
        assertTrue(rewardItems.none { it.slot == EquipmentSlot.ARTIFACT })
        assertEquals(
            InventoryCatalog.victoryEquipmentItemIds.first(),
            InventoryCatalog.nextVictoryEquipmentReward(InventoryCatalog.starterItemIds),
        )
        assertEquals(
            null,
            InventoryCatalog.nextVictoryEquipmentReward(
                InventoryCatalog.starterItemIds + InventoryCatalog.victoryEquipmentItemIds,
            ),
        )
    }

    @Test
    fun resistantVictoryArtifactPoolContainsOnlyNonQuestArtifacts() {
        val rewardItems = InventoryCatalog.items.filter {
            it.id in InventoryCatalog.resistantVictoryArtifactItemIds
        }

        assertEquals(7, rewardItems.size)
        assertTrue(rewardItems.all { it.slot == EquipmentSlot.ARTIFACT })
        assertTrue(rewardItems.none(InventoryItem::questExclusive))
        assertTrue(rewardItems.none { it.id in InventoryCatalog.starterItemIds })
    }

    @Test
    fun expandedCatalogHasVarietyForEverySlotAndBonusType() {
        assertEquals(36, InventoryCatalog.items.size)
        EquipmentSlot.entries.forEach { slot ->
            assertTrue(
                "Expected at least three items for $slot",
                InventoryCatalog.items.count { it.slot == slot } >= 3,
            )
        }
        ItemBonusType.entries.forEach { bonusType ->
            assertTrue(
                "Expected at least one item with $bonusType",
                InventoryCatalog.items.any { item ->
                    item.bonuses.any { it.type == bonusType }
                },
            )
        }
    }
}
