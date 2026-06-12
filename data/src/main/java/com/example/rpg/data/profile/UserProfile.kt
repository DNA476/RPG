package com.example.rpg.data.profile

enum class UserSex {
    MALE,
    FEMALE,
}

data class UserProfile(
    val weightKg: Float? = null,
    val heightCm: Int? = null,
    val sex: UserSex? = null,
)
