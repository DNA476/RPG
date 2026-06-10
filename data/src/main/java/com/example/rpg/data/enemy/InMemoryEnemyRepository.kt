package com.example.rpg.data.enemy

import com.example.rpg.game.enemy.Boss

/**
 * In-memory enemy repository for MVP.
 * Replace the list with JSON/Room-backed configs when adding campaigns and multiple bosses.
 */
class InMemoryEnemyRepository : EnemyRepository {
    private val enemies = listOf(
        EnemyConfig(
            id = "training_dummy",
            name = "Training Dummy",
            maxHp = 10,
            imageResource = "training_dummy",
        ),
    )

    override fun getTrainingBoss(): Boss {
        val config = enemies.first { it.id == "training_dummy" }
        return Boss(
            id = config.id,
            name = config.name,
            maxHp = config.maxHp,
            currentHp = config.maxHp,
            imageResource = config.imageResource,
        )
    }
}
