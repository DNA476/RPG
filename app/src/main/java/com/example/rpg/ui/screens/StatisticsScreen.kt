package com.example.rpg.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.rpg.R
import com.example.rpg.domain.exercise.ExerciseConfig
import com.example.rpg.domain.exercise.ExerciseType
import com.example.rpg.ui.viewmodel.StatisticsPeriod
import com.example.rpg.ui.viewmodel.StatisticsUiState
import com.example.rpg.ui.localization.exerciseNameResource
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.max

@Composable
fun StatisticsScreen(
    statistics: StatisticsUiState,
    exercises: List<ExerciseConfig>,
    onPeriodSelected: (StatisticsPeriod) -> Unit,
    onExerciseSelected: (ExerciseType?) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF17352B), Color(0xFF0E1714), Color(0xFF080A09)),
                ),
            )
            .statusBarsPadding(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            OutlinedButton(onClick = onBack) {
                Text(stringResource(R.string.back))
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.statistics),
                    color = Color(0xFFFFD166),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = stringResource(R.string.statistics_local_note),
                    color = Color.White.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatisticsPeriod.entries.forEach { period ->
                    FilterChip(
                        selected = statistics.period == period,
                        onClick = { onPeriodSelected(period) },
                        label = {
                            Text(
                                text = when (period) {
                                    StatisticsPeriod.LAST_7_DAYS -> stringResource(R.string.period_7_days)
                                    StatisticsPeriod.LAST_30_DAYS -> stringResource(R.string.period_30_days)
                                    StatisticsPeriod.LAST_90_DAYS -> stringResource(R.string.period_90_days)
                                },
                            )
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        item {
            ExerciseFilter(
                exercises = exercises,
                selectedExercise = statistics.selectedExercise,
                onExerciseSelected = onExerciseSelected,
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StatCard(
                    value = statistics.totalRepetitions.toString(),
                    label = stringResource(R.string.repetitions_label),
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    value = statistics.estimatedCalories.toString(),
                    label = stringResource(R.string.calories_approx_label),
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    value = statistics.activeDays.toString(),
                    label = stringResource(R.string.active_days_label),
                    modifier = Modifier.weight(1f),
                )
            }
        }
        if (statistics.usesDefaultWeight) {
            item {
                Text(
                    text = stringResource(R.string.default_weight_long),
                    color = Color.White.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        item {
            StatisticsChart(statistics = statistics)
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = stringResource(R.string.by_type_for_period),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                if (statistics.exerciseSummaries.isEmpty()) {
                    EmptyStatistics()
                } else {
                    statistics.exerciseSummaries.forEach { summary ->
                        val summaryValues = listOfNotNull(
                            summary.repetitions.takeIf { it > 0 }?.let {
                                stringResource(R.string.repetitions_short, it)
                            },
                            summary.activeSeconds.takeIf { it > 0 }?.let {
                                stringResource(R.string.seconds_short, it)
                            },
                        ).joinToString(" · ")
                        Surface(
                            color = Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = stringResource(exerciseNameResource(summary.exerciseType)),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = summaryValues,
                                    color = Color(0xFFFFD166),
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseFilter(
    exercises: List<ExerciseConfig>,
    selectedExercise: ExerciseType?,
    onExerciseSelected: (ExerciseType?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = selectedExercise?.let {
        stringResource(exerciseNameResource(it))
    } ?: stringResource(R.string.all_exercises)
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = selectedName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.all_exercises)) },
                onClick = {
                    expanded = false
                    onExerciseSelected(null)
                },
            )
            exercises.forEach { exercise ->
                DropdownMenuItem(
                    text = { Text(stringResource(exerciseNameResource(exercise.type))) },
                    onClick = {
                        expanded = false
                        onExerciseSelected(exercise.type)
                    },
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = value,
                color = Color(0xFFFFD166),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun StatisticsChart(statistics: StatisticsUiState) {
    val showDuration = statistics.selectedExercise == ExerciseType.PLANK
    val values = statistics.chartPoints.map {
        if (showDuration) it.activeSeconds else it.repetitions
    }
    val maximum = max(values.maxOrNull() ?: 0, 1)
    val configuration = LocalConfiguration.current
    val formatter = remember(configuration) {
        DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())
    }
    val firstDate = statistics.chartPoints.firstOrNull()?.date
    val middleDate = statistics.chartPoints.getOrNull(statistics.chartPoints.size / 2)?.date
    val lastDate = statistics.chartPoints.lastOrNull()?.date
    val chartDescription = stringResource(
        R.string.chart_accessibility,
        values.joinToString(),
    )

    Surface(
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(
                    if (showDuration) R.string.chart_seconds_by_day else R.string.chart_repetitions_by_day,
                ),
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .semantics {
                        contentDescription = chartDescription
                    },
            ) {
                val slotWidth = size.width / max(values.size, 1)
                val barWidth = (slotWidth * 0.62f).coerceAtLeast(2f)
                drawLine(
                    color = Color.White.copy(alpha = 0.18f),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2f,
                )
                values.forEachIndexed { index, value ->
                    if (value == 0) return@forEachIndexed
                    val barHeight = size.height * value / maximum
                    val left = index * slotWidth + (slotWidth - barWidth) / 2f
                    drawRoundRect(
                        color = Color(0xFFFFD166),
                        topLeft = Offset(left, size.height - barHeight),
                        size = Size(barWidth, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f, 5f),
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                listOf(firstDate, middleDate, lastDate).forEach { date ->
                    Text(
                        text = date?.format(formatter).orEmpty(),
                        color = Color.White.copy(alpha = 0.62f),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStatistics() {
    Surface(
        color = Color.White.copy(alpha = 0.06f),
        shape = RoundedCornerShape(16.dp),
    ) {
        Text(
            text = stringResource(R.string.empty_statistics),
            color = Color.White.copy(alpha = 0.72f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
        )
    }
}
