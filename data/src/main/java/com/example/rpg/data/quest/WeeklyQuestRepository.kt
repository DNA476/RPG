package com.example.rpg.data.quest

interface WeeklyQuestRepository {
    fun loadState(): WeeklyQuestState?

    fun saveState(state: WeeklyQuestState)
}
