package com.example.rpg.data.inventory

object InventoryCatalog {
    val items: List<InventoryItem> = listOf(
        InventoryItem(
            id = "novice_headband",
            slot = EquipmentSlot.HEAD,
            rarity = ItemRarity.COMMON,
            iconType = ItemIconType.HEADBAND,
            bonuses = listOf(ItemBonus(ItemBonusType.ATTACK_POWER_PERCENT, 2)),
        ),
        InventoryItem(
            id = "scout_hood",
            slot = EquipmentSlot.HEAD,
            rarity = ItemRarity.COMMON,
            iconType = ItemIconType.HOOD,
            bonuses = listOf(
                ItemBonus(ItemBonusType.OPENING_ATTACK_DAMAGE_PERCENT, 6),
            ),
        ),
        InventoryItem(
            id = "storm_circlet",
            slot = EquipmentSlot.HEAD,
            rarity = ItemRarity.EPIC,
            iconType = ItemIconType.CIRCLET,
            bonuses = listOf(
                ItemBonus(ItemBonusType.ATTACK_POWER_PERCENT, 6),
                ItemBonus(ItemBonusType.ENEMY_ABILITY_DELAY_SECONDS, 2),
            ),
        ),
        InventoryItem(
            id = "iron_training_vest",
            slot = EquipmentSlot.CHEST,
            rarity = ItemRarity.RARE,
            iconType = ItemIconType.VEST,
            bonuses = listOf(
                ItemBonus(ItemBonusType.DEBUFF_DURATION_REDUCTION_PERCENT, 6),
                ItemBonus(ItemBonusType.ATTACK_POWER_PERCENT, 2),
            ),
        ),
        InventoryItem(
            id = "wanderer_cloak",
            slot = EquipmentSlot.CHEST,
            rarity = ItemRarity.COMMON,
            iconType = ItemIconType.CLOAK,
            bonuses = listOf(
                ItemBonus(ItemBonusType.DEBUFF_DURATION_REDUCTION_PERCENT, 5),
            ),
        ),
        InventoryItem(
            id = "bastion_cuirass",
            slot = EquipmentSlot.CHEST,
            rarity = ItemRarity.EPIC,
            iconType = ItemIconType.CUIRASS,
            bonuses = listOf(
                ItemBonus(ItemBonusType.DEBUFF_DURATION_REDUCTION_PERCENT, 12),
                ItemBonus(ItemBonusType.RESISTANT_MATCHUP_DAMAGE_PERCENT, 7),
            ),
        ),
        InventoryItem(
            id = "steady_wraps",
            slot = EquipmentSlot.HANDS,
            rarity = ItemRarity.COMMON,
            iconType = ItemIconType.WRAPS,
            bonuses = listOf(ItemBonus(ItemBonusType.RESISTANT_MATCHUP_DAMAGE_PERCENT, 4)),
        ),
        InventoryItem(
            id = "climbers_grip",
            slot = EquipmentSlot.HANDS,
            rarity = ItemRarity.RARE,
            iconType = ItemIconType.GRIP,
            bonuses = listOf(
                ItemBonus(ItemBonusType.STREAK_DAMAGE_PERCENT, 7),
                ItemBonus(ItemBonusType.ATTACK_POWER_PERCENT, 3),
            ),
        ),
        InventoryItem(
            id = "thunder_fists",
            slot = EquipmentSlot.HANDS,
            rarity = ItemRarity.EPIC,
            iconType = ItemIconType.GAUNTLET,
            bonuses = listOf(
                ItemBonus(ItemBonusType.OPENING_ATTACK_DAMAGE_PERCENT, 14),
                ItemBonus(ItemBonusType.ATTACK_POWER_PERCENT, 5),
            ),
        ),
        InventoryItem(
            id = "runners_leggings",
            slot = EquipmentSlot.LEGS,
            rarity = ItemRarity.RARE,
            iconType = ItemIconType.LEGGINGS,
            bonuses = listOf(ItemBonus(ItemBonusType.ENEMY_ABILITY_DELAY_SECONDS, 1)),
        ),
        InventoryItem(
            id = "pathfinder_trousers",
            slot = EquipmentSlot.LEGS,
            rarity = ItemRarity.COMMON,
            iconType = ItemIconType.TROUSERS,
            bonuses = listOf(
                ItemBonus(ItemBonusType.STREAK_DAMAGE_PERCENT, 5),
            ),
        ),
        InventoryItem(
            id = "stonewall_greaves",
            slot = EquipmentSlot.LEGS,
            rarity = ItemRarity.EPIC,
            iconType = ItemIconType.GREAVES,
            bonuses = listOf(
                ItemBonus(ItemBonusType.DEBUFF_DURATION_REDUCTION_PERCENT, 9),
                ItemBonus(ItemBonusType.RESISTANT_MATCHUP_DAMAGE_PERCENT, 8),
            ),
        ),
        InventoryItem(
            id = "trail_boots",
            slot = EquipmentSlot.FEET,
            rarity = ItemRarity.EPIC,
            iconType = ItemIconType.BOOTS,
            bonuses = listOf(
                ItemBonus(ItemBonusType.ENEMY_ABILITY_DELAY_SECONDS, 2),
                ItemBonus(ItemBonusType.DEBUFF_DURATION_REDUCTION_PERCENT, 8),
            ),
        ),
        InventoryItem(
            id = "novice_sandals",
            slot = EquipmentSlot.FEET,
            rarity = ItemRarity.COMMON,
            iconType = ItemIconType.SANDALS,
            bonuses = listOf(
                ItemBonus(ItemBonusType.WEAKNESS_MATCHUP_DAMAGE_PERCENT, 5),
            ),
        ),
        InventoryItem(
            id = "skybound_boots",
            slot = EquipmentSlot.FEET,
            rarity = ItemRarity.EPIC,
            iconType = ItemIconType.WINGED_BOOTS,
            bonuses = listOf(
                ItemBonus(ItemBonusType.ENEMY_ABILITY_DELAY_SECONDS, 3),
                ItemBonus(ItemBonusType.OPENING_ATTACK_DAMAGE_PERCENT, 9),
            ),
        ),
        InventoryItem(
            id = "oak_training_blade",
            slot = EquipmentSlot.WEAPON,
            rarity = ItemRarity.COMMON,
            iconType = ItemIconType.SWORD,
            bonuses = listOf(ItemBonus(ItemBonusType.ATTACK_POWER_PERCENT, 3)),
        ),
        InventoryItem(
            id = "ember_edge",
            slot = EquipmentSlot.WEAPON,
            rarity = ItemRarity.EPIC,
            iconType = ItemIconType.FLAMING_SWORD,
            bonuses = listOf(
                ItemBonus(ItemBonusType.ATTACK_POWER_PERCENT, 7),
                ItemBonus(ItemBonusType.RESISTANT_MATCHUP_DAMAGE_PERCENT, 8),
            ),
        ),
        InventoryItem(
            id = "scout_dagger",
            slot = EquipmentSlot.WEAPON,
            rarity = ItemRarity.COMMON,
            iconType = ItemIconType.DAGGER,
            bonuses = listOf(
                ItemBonus(ItemBonusType.OPENING_ATTACK_DAMAGE_PERCENT, 8),
            ),
        ),
        InventoryItem(
            id = "stonebreaker_hammer",
            slot = EquipmentSlot.WEAPON,
            rarity = ItemRarity.RARE,
            iconType = ItemIconType.HAMMER,
            bonuses = listOf(
                ItemBonus(ItemBonusType.RESISTANT_MATCHUP_DAMAGE_PERCENT, 9),
                ItemBonus(ItemBonusType.ATTACK_POWER_PERCENT, 4),
            ),
        ),
        InventoryItem(
            id = "ashwood_staff",
            slot = EquipmentSlot.WEAPON,
            rarity = ItemRarity.RARE,
            iconType = ItemIconType.STAFF,
            bonuses = listOf(
                ItemBonus(ItemBonusType.ENEMY_ABILITY_DELAY_SECONDS, 2),
                ItemBonus(ItemBonusType.WEAKNESS_MATCHUP_DAMAGE_PERCENT, 8),
            ),
        ),
        InventoryItem(
            id = "resolve_stone",
            slot = EquipmentSlot.ARTIFACT,
            rarity = ItemRarity.RARE,
            iconType = ItemIconType.STONE,
            bonuses = listOf(ItemBonus(ItemBonusType.DEBUFF_DURATION_REDUCTION_PERCENT, 10)),
        ),
        InventoryItem(
            id = "echo_charm",
            slot = EquipmentSlot.ARTIFACT,
            rarity = ItemRarity.EPIC,
            iconType = ItemIconType.CHARM,
            bonuses = listOf(
                ItemBonus(ItemBonusType.RESISTANT_MATCHUP_DAMAGE_PERCENT, 10),
                ItemBonus(ItemBonusType.ENEMY_ABILITY_DELAY_SECONDS, 1),
            ),
        ),
        InventoryItem(
            id = "copper_compass",
            slot = EquipmentSlot.ARTIFACT,
            rarity = ItemRarity.COMMON,
            iconType = ItemIconType.COMPASS,
            bonuses = listOf(
                ItemBonus(ItemBonusType.WEAKNESS_MATCHUP_DAMAGE_PERCENT, 6),
            ),
        ),
        InventoryItem(
            id = "ember_core",
            slot = EquipmentSlot.ARTIFACT,
            rarity = ItemRarity.RARE,
            iconType = ItemIconType.CORE,
            bonuses = listOf(
                ItemBonus(ItemBonusType.ATTACK_POWER_PERCENT, 4),
                ItemBonus(ItemBonusType.STREAK_DAMAGE_PERCENT, 6),
            ),
        ),
        InventoryItem(
            id = "moon_hourglass",
            slot = EquipmentSlot.ARTIFACT,
            rarity = ItemRarity.RARE,
            iconType = ItemIconType.HOURGLASS,
            bonuses = listOf(
                ItemBonus(ItemBonusType.ENEMY_ABILITY_DELAY_SECONDS, 2),
                ItemBonus(ItemBonusType.DEBUFF_DURATION_REDUCTION_PERCENT, 6),
            ),
        ),
        InventoryItem(
            id = "watchers_eye",
            slot = EquipmentSlot.ARTIFACT,
            rarity = ItemRarity.EPIC,
            iconType = ItemIconType.EYE,
            bonuses = listOf(
                ItemBonus(ItemBonusType.WEAKNESS_MATCHUP_DAMAGE_PERCENT, 13),
                ItemBonus(ItemBonusType.OPENING_ATTACK_DAMAGE_PERCENT, 10),
            ),
        ),
        InventoryItem(
            id = "phoenix_feather",
            slot = EquipmentSlot.ARTIFACT,
            rarity = ItemRarity.EPIC,
            iconType = ItemIconType.FEATHER,
            bonuses = listOf(
                ItemBonus(ItemBonusType.STREAK_DAMAGE_PERCENT, 12),
                ItemBonus(ItemBonusType.ATTACK_POWER_PERCENT, 5),
            ),
        ),
        InventoryItem(
            id = "trailblazer_greaves",
            slot = EquipmentSlot.LEGS,
            rarity = ItemRarity.RARE,
            iconType = ItemIconType.GREAVES,
            bonuses = listOf(
                ItemBonus(ItemBonusType.STREAK_DAMAGE_PERCENT, 8),
                ItemBonus(ItemBonusType.ENEMY_ABILITY_DELAY_SECONDS, 1),
            ),
            questExclusive = true,
        ),
        InventoryItem(
            id = "aegis_compass",
            slot = EquipmentSlot.ARTIFACT,
            rarity = ItemRarity.EPIC,
            iconType = ItemIconType.COMPASS,
            bonuses = listOf(
                ItemBonus(ItemBonusType.RESISTANT_MATCHUP_DAMAGE_PERCENT, 12),
                ItemBonus(ItemBonusType.DEBUFF_DURATION_REDUCTION_PERCENT, 8),
            ),
            questExclusive = true,
        ),
        InventoryItem(
            id = "titan_cuirass",
            slot = EquipmentSlot.CHEST,
            rarity = ItemRarity.LEGENDARY,
            iconType = ItemIconType.CUIRASS,
            bonuses = listOf(
                ItemBonus(ItemBonusType.DEBUFF_DURATION_REDUCTION_PERCENT, 14),
                ItemBonus(ItemBonusType.RESISTANT_MATCHUP_DAMAGE_PERCENT, 12),
                ItemBonus(ItemBonusType.ATTACK_POWER_PERCENT, 6),
            ),
            questExclusive = true,
        ),
        InventoryItem(
            id = "focus_charm",
            slot = EquipmentSlot.ARTIFACT,
            rarity = ItemRarity.RARE,
            iconType = ItemIconType.CHARM,
            bonuses = listOf(
                ItemBonus(ItemBonusType.WEAKNESS_MATCHUP_DAMAGE_PERCENT, 9),
                ItemBonus(ItemBonusType.OPENING_ATTACK_DAMAGE_PERCENT, 6),
            ),
            questExclusive = true,
        ),
        InventoryItem(
            id = "skybreaker_staff",
            slot = EquipmentSlot.WEAPON,
            rarity = ItemRarity.EPIC,
            iconType = ItemIconType.STAFF,
            bonuses = listOf(
                ItemBonus(ItemBonusType.ENEMY_ABILITY_DELAY_SECONDS, 3),
                ItemBonus(ItemBonusType.STREAK_DAMAGE_PERCENT, 10),
            ),
            questExclusive = true,
        ),
        InventoryItem(
            id = "sunforged_crown",
            slot = EquipmentSlot.HEAD,
            rarity = ItemRarity.LEGENDARY,
            iconType = ItemIconType.CROWN,
            bonuses = listOf(
                ItemBonus(ItemBonusType.ATTACK_POWER_PERCENT, 12),
                ItemBonus(ItemBonusType.OPENING_ATTACK_DAMAGE_PERCENT, 14),
                ItemBonus(ItemBonusType.WEAKNESS_MATCHUP_DAMAGE_PERCENT, 10),
            ),
            questExclusive = true,
        ),
        InventoryItem(
            id = "guardian_wraps",
            slot = EquipmentSlot.HANDS,
            rarity = ItemRarity.RARE,
            iconType = ItemIconType.WRAPS,
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
            iconType = ItemIconType.HAMMER,
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
            iconType = ItemIconType.CROWN,
            bonuses = listOf(
                ItemBonus(ItemBonusType.ATTACK_POWER_PERCENT, 10),
                ItemBonus(ItemBonusType.DEBUFF_DURATION_REDUCTION_PERCENT, 10),
                ItemBonus(ItemBonusType.RESISTANT_MATCHUP_DAMAGE_PERCENT, 10),
            ),
            questExclusive = true,
        ),
    )

    val demoOwnedItemIds: Set<String> = items
        .filter { item ->
            !item.questExclusive && item.slot != EquipmentSlot.ARTIFACT
        }
        .mapTo(linkedSetOf(), InventoryItem::id)

    val resistantVictoryArtifactItemIds: Set<String> = items
        .filter { item ->
            item.slot == EquipmentSlot.ARTIFACT && !item.questExclusive
        }
        .mapTo(linkedSetOf(), InventoryItem::id)

    fun get(id: String): InventoryItem? = items.firstOrNull { it.id == id }
}
