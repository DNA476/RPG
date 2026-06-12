package com.example.rpg.data.profile

interface UserProfileRepository {
    fun isOnboardingCompleted(): Boolean

    fun loadProfile(): UserProfile

    fun saveProfile(profile: UserProfile)

    fun completeOnboarding()
}
