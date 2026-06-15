package com.example.rpg.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.rpg.data.inventory.EquipmentSlot
import com.example.rpg.data.inventory.InventoryCatalog
import com.example.rpg.data.inventory.InventoryRepository
import com.example.rpg.data.inventory.InventoryState
import org.json.JSONArray
import org.json.JSONObject

class SharedPreferencesInventoryRepository(
    private val preferences: SharedPreferences,
) : InventoryRepository {
    override fun loadInventory(): InventoryState {
        val storedJson = preferences.getString(KEY_INVENTORY, null)
            ?: return InventoryState(ownedItemIds = InventoryCatalog.demoOwnedItemIds)
        return runCatching {
            val root = JSONObject(storedJson)
            val ownedIds = root.optJSONArray(JSON_OWNED_ITEMS)
                .toStringSet()
                .filterTo(linkedSetOf()) { InventoryCatalog.get(it) != null }
            val equippedItems = buildMap {
                val equipped = root.optJSONObject(JSON_EQUIPPED_ITEMS) ?: JSONObject()
                EquipmentSlot.entries.forEach { slot ->
                    val itemId = equipped.optString(slot.name)
                    val item = InventoryCatalog.get(itemId)
                    if (item != null && item.slot == slot && itemId in ownedIds) {
                        put(slot, itemId)
                    }
                }
            }
            InventoryState(
                ownedItemIds = ownedIds.ifEmpty { InventoryCatalog.demoOwnedItemIds },
                equippedItemIds = equippedItems,
            )
        }.getOrElse {
            InventoryState(ownedItemIds = InventoryCatalog.demoOwnedItemIds)
        }
    }

    override fun saveInventory(inventory: InventoryState) {
        val ownedItems = JSONArray()
        inventory.ownedItemIds.forEach(ownedItems::put)
        val equippedItems = JSONObject()
        inventory.equippedItemIds.forEach { (slot, itemId) ->
            equippedItems.put(slot.name, itemId)
        }
        val root = JSONObject()
            .put(JSON_OWNED_ITEMS, ownedItems)
            .put(JSON_EQUIPPED_ITEMS, equippedItems)
        preferences.edit {
            putString(KEY_INVENTORY, root.toString())
        }
    }

    private fun JSONArray?.toStringSet(): Set<String> {
        if (this == null) return emptySet()
        return buildSet {
            for (index in 0 until length()) {
                optString(index).takeIf(String::isNotBlank)?.let(::add)
            }
        }
    }

    companion object {
        private const val KEY_INVENTORY = "player_inventory"
        private const val JSON_OWNED_ITEMS = "ownedItems"
        private const val JSON_EQUIPPED_ITEMS = "equippedItems"
    }
}
