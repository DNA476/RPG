package com.example.rpg.data.inventory

interface InventoryRepository {
    fun loadInventory(): InventoryState

    fun saveInventory(inventory: InventoryState)
}
