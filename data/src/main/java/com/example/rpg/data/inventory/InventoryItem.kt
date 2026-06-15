package com.example.rpg.data.inventory

enum class ItemRarity {
    COMMON,
    RARE,
    EPIC,
    LEGENDARY,
}

enum class ItemBonusType {
    ATTACK_POWER_PERCENT,
    DEBUFF_DURATION_REDUCTION_PERCENT,
    ENEMY_ABILITY_DELAY_SECONDS,
    RESISTANT_MATCHUP_DAMAGE_PERCENT,
}

data class ItemBonus(
    val type: ItemBonusType,
    val value: Int,
)

data class InventoryItem(
    val id: String,
    val slot: EquipmentSlot,
    val rarity: ItemRarity,
    val bonuses: List<ItemBonus>,
    val questExclusive: Boolean = false,
)
