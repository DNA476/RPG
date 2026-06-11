package com.example.rpg.game.enemy

/**
 * Enemy subtype reserved for boss-specific mechanics and later progression tiers.
 */
class Boss(
    id: String,
    name: String,
    description: String,
    maxHp: Int,
    currentHp: Int,
    imageResource: String,
    weakness: ExerciseAffinity,
    resistance: ExerciseAffinity,
    ability: EnemyAbility,
) : Enemy(
    id = id,
    name = name,
    description = description,
    maxHp = maxHp,
    currentHp = currentHp,
    imageResource = imageResource,
    weakness = weakness,
    resistance = resistance,
    ability = ability,
)
