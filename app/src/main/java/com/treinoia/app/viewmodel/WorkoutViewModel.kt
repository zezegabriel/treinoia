package com.treinoia.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treinoia.app.data.ClaudeApiClient
import com.treinoia.app.data.ClaudeApiException
import com.treinoia.app.data.Equipamento
import com.treinoia.app.data.EQUIPAMENTOS_DISPONIVEIS
import com.treinoia.app.data.FocoTreino
import com.treinoia.app.data.Nivel
import com.treinoia.app.data.Objetivo
import com.treinoia.app.data.PreferencesManager
import com.treinoia.app.data.Workout
import com.treinoia.app.data.WorkoutRequest
import kotlinx.coroutines.launch

/** Qual tela está visível no momento. */
sealed interface Screen {
    data object Form : Screen
    data object Loading : Screen
    data class Result(val workout: Workout, val badge: String) : Screen
    data class Error(val message: String) : Screen
    data object Config : Screen
}

/** Estado do formulário — os campos que o usuário preenche. */
class FormState {
    var objetivo by mutableStateOf(Objetivo.HIPERTROFIA)
    var nivel by mutableStateOf(Nivel.INICIANTE)
    var diasPorSemana by mutableStateOf(4)
    var focoHoje by mutableStateOf(FocoTreino.CORPO_TODO)
    var duracaoMin by mutableStateOf(60)
    var restricoes by mutableStateOf("")
    var equipamentos by mutableStateOf(
        EQUIPAMENTOS_DISPONIVEIS.filter { it.padraoSelecionado }.map { it.valor }.toSet()
    )

    fun toggleEquipamento(valor: String) {
        equipamentos = if (valor in equipamentos) equipamentos - valor else equipamentos + valor
    }
}

class WorkoutViewModel(private val prefs: PreferencesManager) : ViewModel() {

    var screen: Screen by mutableStateOf(Screen.Form)
        private set

    val formState = FormState()

    var apiKeyInput by mutableStateOf(prefs.getApiKey() ?: "")

    fun abrirConfig() {
        apiKeyInput = prefs.getApiKey() ?: ""
        screen = Screen.Config
    }

    fun salvarApiKey() {
        val key = apiKeyInput.trim()
        if (key.isNotEmpty()) {
            prefs.saveApiKey(key)
        }
        screen = Screen.Form
    }

    fun voltarParaForm() {
        screen = Screen.Form
    }

    fun gerarTreino() {
        val apiKey = prefs.getApiKey()
        if (apiKey.isNullOrBlank()) {
            abrirConfig()
            return
        }

        val request = WorkoutRequest(
            objetivo = formState.objetivo.valor,
            nivel = formState.nivel.valor,
            diasPorSemana = formState.diasPorSemana,
            focoHoje = formState.focoHoje.valor,
            equipamentos = formState.equipamentos.toList(),
            restricoes = formState.restricoes.trim(),
            duracaoMin = formState.duracaoMin
        )

        screen = Screen.Loading

        viewModelScope.launch {
            try {
                val workout = ClaudeApiClient.gerarTreino(request, apiKey)
                screen = Screen.Result(workout, formState.focoHoje.badge)
            } catch (e: ClaudeApiException) {
                screen = Screen.Error(e.message ?: "Erro desconhecido ao gerar o treino.")
            } catch (e: Exception) {
                screen = Screen.Error(e.message ?: "Erro desconhecido ao gerar o treino.")
            }
        }
    }

    fun equipamentosDisponiveis(): List<Equipamento> = EQUIPAMENTOS_DISPONIVEIS
}
