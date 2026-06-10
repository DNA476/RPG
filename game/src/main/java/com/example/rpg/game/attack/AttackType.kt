package com.example.rpg.game.attack

/**
 * Attack category produced from exercise events.
 * Future abilities can add modifiers without changing exercise detectors.
 */
sealed class AttackType(
    val id: String,
    val displayName: String,
    val baseDamage: Int,
) {
    /** Basic low-cost attack used by squats in the MVP. */
    data object BasicAttack : AttackType("basic_attack", "Basic Attack", 1)

    /** Placeholder for heavier exercise-driven attacks such as lunges. */
    data object HeavyAttack : AttackType("heavy_attack", "Heavy Attack", 2)

    /** Placeholder for high-impact critical attacks such as jumps. */
    data object CriticalAttack : AttackType("critical_attack", "Critical Attack", 3)
}
