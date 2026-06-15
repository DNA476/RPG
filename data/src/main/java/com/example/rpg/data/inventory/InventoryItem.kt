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
    WEAKNESS_MATCHUP_DAMAGE_PERCENT,
    OPENING_ATTACK_DAMAGE_PERCENT,
    STREAK_DAMAGE_PERCENT,
}

enum class ItemIconType {
    HEADBAND,
    HOOD,
    CIRCLET,
    CROWN,
    VEST,
    CLOAK,
    CUIRASS,
    WRAPS,
    GRIP,
    GAUNTLET,
    LEGGINGS,
    TROUSERS,
    GREAVES,
    BOOTS,
    SANDALS,
    WINGED_BOOTS,
    SWORD,
    FLAMING_SWORD,
    DAGGER,
    HAMMER,
    STAFF,
    STONE,
    CHARM,
    COMPASS,
    CORE,
    HOURGLASS,
    EYE,
    FEATHER,
}

data class ItemBonus(
    val type: ItemBonusType,
    val value: Int,
)

data class InventoryItem(
    val id: String,
    val slot: EquipmentSlot,
    val rarity: ItemRarity,
    val iconType: ItemIconType,
    val bonuses: List<ItemBonus>,
    val questExclusive: Boolean = false,
)
