package com.treinoia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.treinoia.app.data.PreferencesManager
import com.treinoia.app.ui.screens.ConfigScreen
import com.treinoia.app.ui.screens.ErrorScreen
import com.treinoia.app.ui.screens.FormScreen
import com.treinoia.app.ui.screens.LoadingScreen
import com.treinoia.app.ui.screens.ResultScreen
import com.treinoia.app.ui.theme.Iron
import com.treinoia.app.ui.theme.TreinoIATheme
import com.treinoia.app.viewmodel.Screen
import com.treinoia.app.viewmodel.WorkoutViewModel

class WorkoutViewModelFactory(private val prefs: PreferencesManager) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WorkoutViewModel(prefs) as T
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = PreferencesManager(applicationContext)

        setContent {
            val viewModel: WorkoutViewModel = viewModel(factory = WorkoutViewModelFactory(prefs))

            TreinoIATheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TreinoApp(viewModel)
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun TreinoApp(viewModel: WorkoutViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader()

        Box(modifier = Modifier.fillMaxSize()) {
            Crossfade(targetState = viewModel.screen, label = "screen-transition") { screen ->
                when (screen) {
                    is Screen.Form -> FormScreen(viewModel)
                    is Screen.Loading -> LoadingScreen()
                    is Screen.Result -> ResultScreen(
                        workout = screen.workout,
                        badge = screen.badge,
                        onNovoTreino = { viewModel.voltarParaForm() }
                    )
                    is Screen.Error -> ErrorScreen(
                        message = screen.message,
                        onTentarDeNovo = { viewModel.voltarParaForm() }
                    )
                    is Screen.Config -> ConfigScreen(viewModel)
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun AppHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 20.dp)
    ) {
        Text(
            "TREINO DE ACADEMIA SOB MEDIDA",
            style = MaterialTheme.typography.bodyMedium,
            color = Iron
        )
        Text(
            "TreinoIA",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(Iron)
    )
}
