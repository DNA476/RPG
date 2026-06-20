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
            ?: return InventoryState(ownedItemIds = InventoryCatalog.starterItemIds).also {
                markArtifactStartMigrationDone()
                markProductionInventoryMigrationDone()
            }
        return runCatching {
            val needsProductionInventoryMigration =
                preferences.getInt(KEY_INVENTORY_MIGRATION_VERSION, 0) < PRODUCTION_INVENTORY_VERSION
            val root = JSONObject(storedJson)
            val storedOwnedIds = root.optJSONArray(JSON_OWNED_ITEMS)
                .toStringSet()
                .filterTo(linkedSetOf()) { InventoryCatalog.get(it) != null }
            val migratedOwnedIds = removeLegacyDemoArtifactsIfNeeded(storedOwnedIds)
            val storedEquippedIds = buildSet {
                val equipped = root.optJSONObject(JSON_EQUIPPED_ITEMS) ?: JSONObject()
                EquipmentSlot.entries.forEach { slot ->
                    equipped.optString(slot.name).takeIf(String::isNotBlank)?.let(::add)
                }
            }
            val ownedIds = linkedSetOf<String>().apply {
                addAll(migrateProductionInventory(migratedOwnedIds, storedEquippedIds))
                addAll(InventoryCatalog.starterItemIds)
            }
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
                ownedItemIds = ownedIds,
                equippedItemIds = equippedItems,
            ).also { inventory ->
                if (needsProductionInventoryMigration) {
                    saveInventory(inventory)
                    markProductionInventoryMigrationDone()
                }
            }
        }.getOrElse {
            InventoryState(ownedItemIds = InventoryCatalog.starterItemIds).also {
                markArtifactStartMigrationDone()
                markProductionInventoryMigrationDone()
            }
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

    private fun removeLegacyDemoArtifactsIfNeeded(ownedItemIds: Set<String>): Set<String> {
        if (preferences.getBoolean(KEY_ARTIFACT_START_MIGRATION_DONE, false)) {
            return ownedItemIds
        }
        markArtifactStartMigrationDone()
        return ownedItemIds - InventoryCatalog.resistantVictoryArtifactItemIds
    }

    private fun markArtifactStartMigrationDone() {
        if (!preferences.getBoolean(KEY_ARTIFACT_START_MIGRATION_DONE, false)) {
            preferences.edit {
                putBoolean(KEY_ARTIFACT_START_MIGRATION_DONE, true)
            }
        }
    }

    private fun migrateProductionInventory(
        ownedItemIds: Set<String>,
        equippedItemIds: Set<String>,
    ): Set<String> {
        if (preferences.getInt(KEY_INVENTORY_MIGRATION_VERSION, 0) >= PRODUCTION_INVENTORY_VERSION) {
            return ownedItemIds
        }
        if (!ownedItemIds.containsAll(InventoryCatalog.legacyDemoOwnedItemIds)) {
            return ownedItemIds
        }
        return ownedItemIds.filterTo(linkedSetOf()) { itemId ->
            val item = InventoryCatalog.get(itemId)
            itemId in InventoryCatalog.starterItemIds ||
                itemId in equippedItemIds ||
                item?.questExclusive == true ||
                item?.slot == EquipmentSlot.ARTIFACT
        }
    }

    private fun markProductionInventoryMigrationDone() {
        preferences.edit {
            putInt(KEY_INVENTORY_MIGRATION_VERSION, PRODUCTION_INVENTORY_VERSION)
        }
    }

    companion object {
        private const val KEY_INVENTORY = "player_inventory"
        private const val KEY_ARTIFACT_START_MIGRATION_DONE = "artifact_start_migration_done"
        private const val KEY_INVENTORY_MIGRATION_VERSION = "inventory_migration_version"
        private const val PRODUCTION_INVENTORY_VERSION = 1
        private const val JSON_OWNED_ITEMS = "ownedItems"
        private const val JSON_EQUIPPED_ITEMS = "equippedItems"
    }
}
