package com.example.rpg.data

import com.example.rpg.data.profile.UserProfileRepository
import com.example.rpg.data.statistics.ExerciseStatisticsRepository

interface FitnessRepository : UserProfileRepository, ExerciseStatisticsRepository
