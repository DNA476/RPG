package com.example.rpg.data.inventory

data class InventoryState(
    val ownedItemIds: Set<String> = emptySet(),
    val equippedItemIds: Map<EquipmentSlot, String> = emptyMap(),
) {
    fun addItem(itemId: String): InventoryState =
        copy(ownedItemIds = ownedItemIds + itemId)

    fun equip(item: InventoryItem): InventoryState {
        if (item.id !in ownedItemIds) return this
        return copy(equippedItemIds = equippedItemIds + (item.slot to item.id))
    }

    fun unequip(slot: EquipmentSlot): InventoryState =
        copy(equippedItemIds = equippedItemIds - slot)
}
