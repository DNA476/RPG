package com.example.rpg.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.rpg.data.quest.WeeklyQuestRepository
import com.example.rpg.data.quest.WeeklyQuestState
import org.json.JSONObject

class SharedPreferencesWeeklyQuestRepository(
    private val preferences: SharedPreferences,
) : WeeklyQuestRepository {
    override fun loadState(): WeeklyQuestState? {
        val storedJson = preferences.getString(KEY_WEEKLY_QUESTS, null) ?: return null
        return runCatching {
            val root = JSONObject(storedJson)
            val progress = buildMap {
                val progressJson = root.optJSONObject(JSON_PROGRESS) ?: JSONObject()
                progressJson.keys().forEach { questId ->
                    put(questId, progressJson.optInt(questId).coerceAtLeast(0))
                }
            }
            val rewarded = buildSet {
                val rewardedJson = root.optJSONObject(JSON_REWARDED) ?: JSONObject()
                rewardedJson.keys().forEach { questId ->
                    if (rewardedJson.optBoolean(questId)) add(questId)
                }
            }
            WeeklyQuestState(
                weekId = root.getString(JSON_WEEK_ID),
                progressByQuestId = progress,
                rewardedQuestIds = rewarded,
            )
        }.getOrNull()
    }

    override fun saveState(state: WeeklyQuestState) {
        val progress = JSONObject()
        state.progressByQuestId.forEach { (questId, value) ->
            progress.put(questId, value)
        }
        val rewarded = JSONObject()
        state.rewardedQuestIds.forEach { questId ->
            rewarded.put(questId, true)
        }
        val root = JSONObject()
            .put(JSON_WEEK_ID, state.weekId)
            .put(JSON_PROGRESS, progress)
            .put(JSON_REWARDED, rewarded)
        preferences.edit {
            putString(KEY_WEEKLY_QUESTS, root.toString())
        }
    }

    companion object {
        private const val KEY_WEEKLY_QUESTS = "weekly_quests"
        private const val JSON_WEEK_ID = "weekId"
        private const val JSON_PROGRESS = "progress"
        private const val JSON_REWARDED = "rewarded"
    }
}
