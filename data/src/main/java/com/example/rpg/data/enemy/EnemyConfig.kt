package com.example.rpg.data.enemy

/**
 * Serializable-friendly enemy configuration that can later be loaded from JSON, Room, or remote config.
 */
data class EnemyConfig(
    val id: String,
    val name: String,
    val maxHp: Int,
    val imageResource: String,
)
