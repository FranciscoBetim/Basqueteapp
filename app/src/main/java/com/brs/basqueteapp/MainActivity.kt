package com.brs.basqueteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.brs.basqueteapp.ui.GameViewModel
import com.brs.basqueteapp.ui.Screen
import com.brs.basqueteapp.ui.BoxScoreScreen
import com.brs.basqueteapp.ui.SetupScreen
import com.brs.basqueteapp.ui.TrackingScreen
import com.brs.basqueteapp.ui.theme.BasqueteAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BasqueteAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppRoot()
                }
            }
        }
    }
}

@Composable
private fun AppRoot() {
    val vm: GameViewModel = viewModel()

    // Navegação por botão voltar do sistema.
    BackHandler(enabled = vm.screen != Screen.SETUP) {
        when (vm.screen) {
            Screen.BOX -> vm.goTo(Screen.TRACK)
            Screen.SETUP -> Unit
            Screen.TRACK -> if (vm.state.started) vm.goTo(Screen.SETUP) else Unit
        }
    }

    when (vm.screen) {
        Screen.SETUP -> SetupScreen(vm)
        Screen.TRACK -> TrackingScreen(vm)
        Screen.BOX -> BoxScoreScreen(vm)
    }
}
