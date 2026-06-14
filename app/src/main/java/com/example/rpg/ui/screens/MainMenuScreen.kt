package com.example.rpg.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rpg.domain.exercise.ExerciseConfig
import com.example.rpg.domain.exercise.ExerciseType
import com.example.rpg.ui.components.ExerciseCard
import kotlinx.coroutines.launch

@Composable
fun MainMenuScreen(
    exercises: List<ExerciseConfig>,
    selectedExercise: ExerciseConfig?,
    todayEstimatedCalories: Int,
    todayHasActivity: Boolean,
    usesDefaultWeight: Boolean,
    onExerciseSelected: (ExerciseType) -> Unit,
    onContinue: () -> Unit,
    onStatistics: () -> Unit,
    onProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MainMenuDrawer(
                onStatistics = onStatistics,
                onProfile = onProfile,
            )
        },
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF17352B), Color(0xFF0E1714), Color(0xFF080A09)),
                    ),
                )
                .statusBarsPadding(),
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        drawerState.open()
                                    }
                                },
                                modifier = Modifier.semantics {
                                    contentDescription = "Открыть меню"
                                },
                            ) {
                                HamburgerIcon()
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "FitRPG",
                                color = Color(0xFFFFD166),
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Black,
                            )
                        }
                        Text(
                            text = "Побеждай врагов, выполняя реальные упражнения перед камерой",
                            color = Color.White.copy(alpha = 0.82f),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
                item {
                    TodayCaloriesCard(
                        calories = todayEstimatedCalories,
                        hasActivity = todayHasActivity,
                        usesDefaultWeight = usesDefaultWeight,
                        onClick = onStatistics,
                    )
                }
                item {
                    Text(
                        text = "Выбери тренировку",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
                items(exercises, key = { it.type.name }) { exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        selected = exercise.type == selectedExercise?.type,
                        onClick = { onExerciseSelected(exercise.type) },
                    )
                }
            }
            Button(
                onClick = onContinue,
                enabled = selectedExercise != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD166),
                    contentColor = Color(0xFF111111),
                ),
                contentPadding = PaddingValues(vertical = 16.dp),
            ) {
                Text(
                    text = "Выбрать противника",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun MainMenuDrawer(
    onStatistics: () -> Unit,
    onProfile: () -> Unit,
) {
    ModalDrawerSheet(
        modifier = Modifier
            .width(320.dp)
            .navigationBarsPadding(),
        drawerContainerColor = Color(0xFF10251E),
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = "FitRPG",
                color = Color(0xFFFFD166),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 24.dp),
            )
            NavigationDrawerItem(
                label = { Text("Статистика") },
                selected = false,
                onClick = onStatistics,
            )
            NavigationDrawerItem(
                label = { Text("Профиль") },
                selected = false,
                onClick = onProfile,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Профиль и история тренировок хранятся только на устройстве.",
                color = Color.White.copy(alpha = 0.62f),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 12.dp),
            )
        }
    }
}

@Composable
private fun TodayCaloriesCard(
    calories: Int,
    hasActivity: Boolean,
    usesDefaultWeight: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.White.copy(alpha = 0.09f),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = if (hasActivity) {
                    "≈ $calories ккал сегодня"
                } else {
                    "Сегодня ещё нет активности"
                },
                color = Color(0xFFFFD166),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = "Оценка по выполненным упражнениям",
                color = Color.White.copy(alpha = 0.78f),
                style = MaterialTheme.typography.bodyMedium,
            )
            if (usesDefaultWeight) {
                Text(
                    text = "Расчёт для условного веса 70 кг",
                    color = Color.White.copy(alpha = 0.58f),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun HamburgerIcon() {
    Canvas(modifier = Modifier.size(28.dp)) {
        val strokeWidth = 3.dp.toPx()
        val startX = size.width * 0.14f
        val endX = size.width * 0.86f
        listOf(0.28f, 0.5f, 0.72f).forEach { yFraction ->
            drawLine(
                color = Color.White,
                start = Offset(startX, size.height * yFraction),
                end = Offset(endX, size.height * yFraction),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
        }
    }
}
