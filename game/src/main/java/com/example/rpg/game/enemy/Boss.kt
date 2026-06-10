package com.example.rpg.game.enemy

/**
 * Enemy subtype reserved for boss-specific mechanics such as phases, rage bars, and unique rewards.
 */
class Boss(
    id: String,
    name: String,
    maxHp: Int,
    currentHp: Int,
    imageResource: String,
) : Enemy(id, name, maxHp, currentHp, imageResource)
