package com.example.rpg.game.enemy

/**
 * Base enemy model for battle logic and future campaign encounters.
 */
open class Enemy(
    val id: String,
    val name: String,
    val maxHp: Int,
    currentHp: Int,
    val imageResource: String,
) {
    var currentHp: Int = currentHp.coerceIn(0, maxHp)
        private set

    /**
     * Applies damage and clamps HP to zero.
     */
    fun receiveDamage(amount: Int) {
        currentHp = (currentHp - amount).coerceAtLeast(0)
    }

    /**
     * Restores enemy HP to maximum for a new battle.
     */
    fun resetHealth() {
        currentHp = maxHp
    }

    /**
     * Returns true when the enemy has no remaining HP.
     */
    fun isDefeated(): Boolean = currentHp <= 0
}
