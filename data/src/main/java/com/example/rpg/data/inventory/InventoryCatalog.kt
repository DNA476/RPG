package com.example.rpg.data.inventory

object InventoryCatalog {
    val items: List<InventoryItem> = listOf(
        InventoryItem(
            id = "novice_headband",
            slot = EquipmentSlot.HEAD,
            rarity = ItemRarity.COMMON,
            bonuses = listOf(ItemBonus(ItemBonusType.ATTACK_POWER_PERCENT, 2)),
        ),
        InventoryItem(
            id = "iron_training_vest",
            slot = EquipmentSlot.CHEST,
            rarity = ItemRarity.RARE,
            bonuses = listOf(
                ItemBonus(ItemBonusType.DEBUFF_DURATION_REDUCTION_PERCENT, 6),
                ItemBonus(ItemBonusType.ATTACK_POWER_PERCENT, 2),
            ),
        ),
        InventoryItem(
            id = "steady_wraps",
            slot = EquipmentSlot.HANDS,
            rarity = ItemRarity.COMMON,
            bonuses = listOf(ItemBonus(ItemBonusType.RESISTANT_MATCHUP_DAMAGE_PERCENT, 4)),
        ),
        InventoryItem(
            id = "runners_leggings",
            slot = EquipmentSlot.LEGS,
            rarity = ItemRarity.RARE,
            bonuses = listOf(ItemBonus(ItemBonusType.ENEMY_ABILITY_DELAY_SECONDS, 1)),
        ),
        InventoryItem(
            id = "trail_boots",
            slot = EquipmentSlot.FEET,
            rarity = ItemRarity.EPIC,
            bonuses = listOf(
                ItemBonus(ItemBonusType.ENEMY_ABILITY_DELAY_SECONDS, 2),
                ItemBonus(ItemBonusType.DEBUFF_DURATION_REDUCTION_PERCENT, 8),
            ),
        ),
        InventoryItem(
            id = "oak_training_blade",
            slot = EquipmentSlot.WEAPON,
            rarity = ItemRarity.COMMON,
            bonuses = listOf(ItemBonus(ItemBonusType.ATTACK_POWER_PERCENT, 3)),
        ),
        InventoryItem(
            id = "ember_edge",
            slot = EquipmentSlot.WEAPON,
            rarity = ItemRarity.EPIC,
            bonuses = listOf(
                ItemBonus(ItemBonusType.ATTACK_POWER_PERCENT, 7),
                ItemBonus(ItemBonusType.RESISTANT_MATCHUP_DAMAGE_PERCENT, 8),
            ),
        ),
        InventoryItem(
            id = "resolve_stone",
            slot = EquipmentSlot.ARTIFACT,
            rarity = ItemRarity.RARE,
            bonuses = listOf(ItemBonus(ItemBonusType.DEBUFF_DURATION_REDUCTION_PERCENT, 10)),
        ),
        InventoryItem(
            id = "echo_charm",
            slot = EquipmentSlot.ARTIFACT,
            rarity = ItemRarity.EPIC,
            bonuses = listOf(
                ItemBonus(ItemBonusType.RESISTANT_MATCHUP_DAMAGE_PERCENT, 10),
                ItemBonus(ItemBonusType.ENEMY_ABILITY_DELAY_SECONDS, 1),
            ),
        ),
        InventoryItem(
            id = "guardian_wraps",
            slot = EquipmentSlot.HANDS,
            rarity = ItemRarity.RARE,
            bonuses = listOf(
                ItemBonus(ItemBonusType.DEBUFF_DURATION_REDUCTION_PERCENT, 8),
                ItemBonus(ItemBonusType.ATTACK_POWER_PERCENT, 3),
            ),
            questExclusive = true,
        ),
        InventoryItem(
            id = "resistance_breaker",
            slot = EquipmentSlot.WEAPON,
            rarity = ItemRarity.EPIC,
            bonuses = listOf(
                ItemBonus(ItemBonusType.RESISTANT_MATCHUP_DAMAGE_PERCENT, 14),
                ItemBonus(ItemBonusType.ATTACK_POWER_PERCENT, 6),
            ),
            questExclusive = true,
        ),
        InventoryItem(
            id = "crown_of_trials",
            slot = EquipmentSlot.HEAD,
            rarity = ItemRarity.LEGENDARY,
            bonuses = listOf(
                ItemBonus(ItemBonusType.ATTACK_POWER_PERCENT, 10),
                ItemBonus(ItemBonusType.DEBUFF_DURATION_REDUCTION_PERCENT, 10),
                ItemBonus(ItemBonusType.RESISTANT_MATCHUP_DAMAGE_PERCENT, 10),
            ),
            questExclusive = true,
        ),
    )

    val demoOwnedItemIds: Set<String> = items
        .filterNot(InventoryItem::questExclusive)
        .mapTo(linkedSetOf(), InventoryItem::id)

    fun get(id: String): InventoryItem? = items.firstOrNull { it.id == id }
}
