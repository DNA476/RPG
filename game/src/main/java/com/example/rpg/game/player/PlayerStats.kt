package com.example.rpg.game.player

/**
 * Player progression container prepared for XP, levels, equipment, inventory, and skill scaling.
 */
data class PlayerStats(
    val level: Int = 1,
    val experience: Int = 0,
    val strength: Int = 1,
)
