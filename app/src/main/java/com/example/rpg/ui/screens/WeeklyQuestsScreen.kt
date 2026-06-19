package com.example.rpg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rpg.R
import com.example.rpg.data.inventory.InventoryCatalog
import com.example.rpg.data.quest.QuestCategory
import com.example.rpg.ui.components.InventoryItemIcon
import com.example.rpg.ui.localization.exerciseNameResource
import com.example.rpg.ui.localization.inventoryItemNameResource
import com.example.rpg.ui.localization.itemRarityResource
import com.example.rpg.ui.localization.questCategoryResource
import com.example.rpg.ui.localization.questTitleResource
import com.example.rpg.ui.viewmodel.QuestUiEntry
import com.example.rpg.ui.viewmodel.WeeklyQuestsUiState

@Composable
fun WeeklyQuestsScreen(
    weeklyQuests: WeeklyQuestsUiState,
    onStartQuest: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutlinedButton(onClick = onBack) {
                Text(stringResource(R.string.back))
            }
            Text(
                text = stringResource(R.string.weekly_quests),
                color = Color(0xFFFFD166),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    text = stringResource(R.string.weekly_quests_subtitle),
                    color = Color.White.copy(alpha = 0.82f),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.weekly_quests_reset,
                        weeklyQuests.daysUntilReset,
                    ),
                    color = Color(0xFFFFD166),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            items(weeklyQuests.entries, key = { it.quest.id }) { entry ->
                WeeklyQuestCard(
                    entry = entry,
                    onStart = { onStartQuest(entry.quest.id) },
                )
            }
            item {
                Text(
                    text = stringResource(R.string.weekly_quests_test_note),
                    color = Color.White.copy(alpha = 0.58f),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }
        }
    }
}

@Composable
private fun WeeklyQuestCard(
    entry: QuestUiEntry,
    onStart: () -> Unit,
) {
    val quest = entry.quest
    val categoryColor = when (quest.category) {
        QuestCategory.REGULAR -> Color(0xFF86CFA5)
        QuestCategory.DIFFICULT -> Color(0xFFB89CFF)
        QuestCategory.CHALLENGE -> Color(0xFFFFD166)
    }
    val reward = requireNotNull(InventoryCatalog.get(quest.rewardItemId))
    val progress = entry.progress.toFloat() / quest.requiredVictories

    Surface(
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                color = categoryColor.copy(alpha = 0.18f),
                shape = RoundedCornerShape(999.dp),
            ) {
                Text(
                    text = stringResource(questCategoryResource(quest.category)),
                    color = categoryColor,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }
            Text(
                text = stringResource(questTitleResource(quest.id)),
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = stringResource(
                    R.string.quest_exercise,
                    stringResource(exerciseNameResource(quest.exerciseType)),
                ),
                color = Color(0xFFFFD166),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = questDescription(entry),
                color = Color.White.copy(alpha = 0.82f),
                style = MaterialTheme.typography.bodyLarge,
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = categoryColor,
                trackColor = Color.White.copy(alpha = 0.14f),
            )
            Text(
                text = stringResource(
                    R.string.quest_progress,
                    entry.progress,
                    quest.requiredVictories,
                ),
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            Surface(
                color = Color.Black.copy(alpha = 0.22f),
                shape = RoundedCornerShape(18.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    InventoryItemIcon(
                        item = reward,
                        modifier = Modifier.size(42.dp),
                        color = categoryColor,
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.quest_reward),
                            color = Color.White.copy(alpha = 0.62f),
                            style = MaterialTheme.typography.labelMedium,
                        )
                        Text(
                            text = stringResource(inventoryItemNameResource(reward)),
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = stringResource(itemRarityResource(reward.rarity)),
                            color = categoryColor,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            Button(
                onClick = onStart,
                enabled = !entry.rewardGranted,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD166),
                    contentColor = Color(0xFF111111),
                ),
                contentPadding = PaddingValues(vertical = 14.dp),
            ) {
                Text(
                    text = stringResource(
                        if (entry.rewardGranted) {
                            R.string.quest_reward_received
                        } else {
                            R.string.quest_go_to_battle
                        },
                    ),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun questDescription(entry: QuestUiEntry): String {
    val quest = entry.quest
    val exercise = stringResource(exerciseNameResource(quest.exerciseType))
    return when {
        quest.requiresResistantEnemy && quest.forbidsArtifacts -> stringResource(
            R.string.quest_goal_resistant_without_artifact,
            quest.requiredVictories,
            exercise,
        )
        quest.requiresResistantEnemy -> stringResource(
            R.string.quest_goal_resistant,
            quest.requiredVictories,
            exercise,
        )
        else -> stringResource(
            R.string.quest_goal_regular,
            quest.requiredVictories,
            exercise,
        )
    }
}
