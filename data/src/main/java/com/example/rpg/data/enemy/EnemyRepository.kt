package com.example.rpg.data.enemy

import com.example.rpg.game.enemy.Boss

/**
 * Source of enemy and boss configurations.
 */
interface EnemyRepository {
    /**
     * Returns the first boss used by the MVP battle.
     */
    fun getTrainingBoss(): Boss
}
