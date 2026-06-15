package com.example.rpg.data.inventory

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class InventoryStateTest {
    private val commonWeapon = requireNotNull(InventoryCatalog.get("oak_training_blade"))
    private val epicWeapon = requireNotNull(InventoryCatalog.get("ember_edge"))

    @Test
    fun equipReplacesItemInSameSlot() {
        val initial = InventoryState(
            ownedItemIds = setOf(commonWeapon.id, epicWeapon.id),
        )

        val equipped = initial
            .equip(commonWeapon)
            .equip(epicWeapon)

        assertEquals(epicWeapon.id, equipped.equippedItemIds[EquipmentSlot.WEAPON])
    }

    @Test
    fun cannotEquipUnownedItem() {
        val initial = InventoryState()

        assertSame(initial, initial.equip(epicWeapon))
    }

    @Test
    fun unequipOnlyClearsRequestedSlot() {
        val artifact = requireNotNull(InventoryCatalog.get("resolve_stone"))
        val initial = InventoryState(
            ownedItemIds = setOf(commonWeapon.id, artifact.id),
        ).equip(commonWeapon).equip(artifact)

        val updated = initial.unequip(EquipmentSlot.WEAPON)

        assertEquals(null, updated.equippedItemIds[EquipmentSlot.WEAPON])
        assertEquals(artifact.id, updated.equippedItemIds[EquipmentSlot.ARTIFACT])
    }
}
