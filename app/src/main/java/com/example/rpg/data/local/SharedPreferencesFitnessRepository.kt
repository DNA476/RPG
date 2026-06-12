package com.example.rpg.data.local

import android.content.SharedPreferences
import com.example.rpg.data.FitnessRepository
import com.example.rpg.data.profile.UserProfile
import com.example.rpg.data.profile.UserSex
import com.example.rpg.data.statistics.DailyExerciseStatistics
import com.example.rpg.domain.exercise.ExerciseType
import java.time.LocalDate
import org.json.JSONArray
import org.json.JSONObject

class SharedPreferencesFitnessRepository(
    private val preferences: SharedPreferences,
) : FitnessRepository {
    override fun isOnboardingCompleted(): Boolean =
        preferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)

    override fun loadProfile(): UserProfile = UserProfile(
        weightKg = preferences.getString(KEY_WEIGHT_KG, null)?.toFloatOrNull(),
        heightCm = preferences.getString(KEY_HEIGHT_CM, null)?.toIntOrNull(),
        sex = preferences.getString(KEY_SEX, null)?.let { storedValue ->
            UserSex.entries.firstOrNull { it.name == storedValue }
        },
    )

    override fun saveProfile(profile: UserProfile) {
        preferences.edit()
            .putOptionalString(KEY_WEIGHT_KG, profile.weightKg?.toString())
            .putOptionalString(KEY_HEIGHT_CM, profile.heightCm?.toString())
            .putOptionalString(KEY_SEX, profile.sex?.name)
            .apply()
    }

    override fun completeOnboarding() {
        preferences.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply()
    }

    @Synchronized
    override fun recordRepetition(
        exerciseType: ExerciseType,
        date: LocalDate,
    ) {
        updateEntry(exerciseType, date) { current ->
            current.copy(repetitions = current.repetitions + 1)
        }
    }

    @Synchronized
    override fun recordActiveSeconds(
        exerciseType: ExerciseType,
        seconds: Int,
        date: LocalDate,
    ) {
        if (seconds <= 0) return
        updateEntry(exerciseType, date) { current ->
            current.copy(activeSeconds = current.activeSeconds + seconds)
        }
    }

    @Synchronized
    override fun getDailyStatistics(): List<DailyExerciseStatistics> =
        readStatistics().sortedWith(
            compareBy<DailyExerciseStatistics> { it.date }
                .thenBy { it.exerciseType.ordinal },
        )

    private fun updateEntry(
        exerciseType: ExerciseType,
        date: LocalDate,
        transform: (DailyExerciseStatistics) -> DailyExerciseStatistics,
    ) {
        val statistics = readStatistics().toMutableList()
        val index = statistics.indexOfFirst {
            it.exerciseType == exerciseType && it.date == date
        }
        val current = statistics.getOrNull(index) ?: DailyExerciseStatistics(
            date = date,
            exerciseType = exerciseType,
        )
        val updated = transform(current)
        if (index >= 0) {
            statistics[index] = updated
        } else {
            statistics += updated
        }
        writeStatistics(statistics)
    }

    private fun readStatistics(): List<DailyExerciseStatistics> {
        val storedJson = preferences.getString(KEY_DAILY_STATISTICS, null) ?: return emptyList()
        return runCatching {
            val entries = JSONArray(storedJson)
            buildList {
                for (index in 0 until entries.length()) {
                    val item = entries.getJSONObject(index)
                    val exerciseType = ExerciseType.entries.firstOrNull {
                        it.name == item.optString(JSON_EXERCISE_TYPE)
                    } ?: continue
                    val date = runCatching {
                        LocalDate.parse(item.getString(JSON_DATE))
                    }.getOrNull() ?: continue
                    add(
                        DailyExerciseStatistics(
                            date = date,
                            exerciseType = exerciseType,
                            repetitions = item.optInt(JSON_REPETITIONS, 0).coerceAtLeast(0),
                            activeSeconds = item.optInt(JSON_ACTIVE_SECONDS, 0).coerceAtLeast(0),
                        ),
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun writeStatistics(statistics: List<DailyExerciseStatistics>) {
        val entries = JSONArray()
        statistics.forEach { entry ->
            entries.put(
                JSONObject()
                    .put(JSON_DATE, entry.date.toString())
                    .put(JSON_EXERCISE_TYPE, entry.exerciseType.name)
                    .put(JSON_REPETITIONS, entry.repetitions)
                    .put(JSON_ACTIVE_SECONDS, entry.activeSeconds),
            )
        }
        preferences.edit().putString(KEY_DAILY_STATISTICS, entries.toString()).apply()
    }

    private fun SharedPreferences.Editor.putOptionalString(
        key: String,
        value: String?,
    ): SharedPreferences.Editor =
        if (value == null) remove(key) else putString(key, value)

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_WEIGHT_KG = "profile_weight_kg"
        private const val KEY_HEIGHT_CM = "profile_height_cm"
        private const val KEY_SEX = "profile_sex"
        private const val KEY_DAILY_STATISTICS = "daily_exercise_statistics"

        private const val JSON_DATE = "date"
        private const val JSON_EXERCISE_TYPE = "exerciseType"
        private const val JSON_REPETITIONS = "repetitions"
        private const val JSON_ACTIVE_SECONDS = "activeSeconds"
    }
}
