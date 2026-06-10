package com.example.rpg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rpg.ui.FitnessRpgApp
import com.example.rpg.ui.theme.RPGTheme
import com.example.rpg.ui.viewmodel.BattleViewModel

/**
 * Android entry point that hosts the Compose fitness-RPG MVP.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RPGTheme {
                val battleViewModel: BattleViewModel = viewModel(
                    factory = BattleViewModel.Factory(application),
                )
                FitnessRpgApp(viewModel = battleViewModel)
            }
        }
    }
}
