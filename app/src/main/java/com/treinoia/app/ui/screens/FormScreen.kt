package com.treinoia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.treinoia.app.data.FocoTreino
import com.treinoia.app.data.Nivel
import com.treinoia.app.data.Objetivo
import com.treinoia.app.ui.components.FormSection
import com.treinoia.app.ui.components.MultiPillGrid
import com.treinoia.app.ui.components.PillSelectorRow
import com.treinoia.app.viewmodel.WorkoutViewModel

private fun iconeObjetivo(o: Objetivo): ImageVector = when (o) {
    Objetivo.HIPERTROFIA -> Icons.Filled.FitnessCenter
    Objetivo.FORCA -> Icons.Filled.Bolt
    Objetivo.EMAGRECIMENTO -> Icons.Filled.LocalFireDepartment
    Objetivo.RESISTENCIA -> Icons.Filled.Repeat
    Objetivo.CONDICIONAMENTO -> Icons.Filled.DirectionsRun
}

@Composable
fun FormScreen(viewModel: WorkoutViewModel) {
    val form = viewModel.formState

    Column(modifier = Modifier.fillMaxSize()) {
        // Conteúdo rolável
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(26.dp)
        ) {
            FormSection(title = "Qual seu objetivo?") {
                PillSelectorRow(
                    options = Objetivo.entries,
                    selected = form.objetivo,
                    label = { it.label },
                    icon = { iconeObjetivo(it) },
                    onSelect = { form.objetivo = it }
                )
            }

            FormSection(title = "Nível de experiência") {
                PillSelectorRow(
                    options = Nivel.entries,
                    selected = form.nivel,
                    label = { it.label },
                    onSelect = { form.nivel = it }
                )
            }

            FormSection(title = "Foco do treino de hoje") {
                PillSelectorRow(
                    options = FocoTreino.entries,
                    selected = form.focoHoje,
                    label = { it.label },
                    onSelect = { form.focoHoje = it }
                )
            }

            FormSection(title = "Dias de treino por semana") {
                PillSelectorRow(
                    options = listOf(2, 3, 4, 5, 6),
                    selected = form.diasPorSemana,
                    label = { "$it dias" },
                    onSelect = { form.diasPorSemana = it }
                )
            }

            FormSection(title = "Tempo disponível hoje") {
                PillSelectorRow(
                    options = listOf(30, 45, 60, 90),
                    selected = form.duracaoMin,
                    label = { "$it min" },
                    onSelect = { form.duracaoMin = it }
                )
            }

            FormSection(
                title = "Equipamentos disponíveis",
                subtitle = "Toque para marcar/desmarcar o que tem na sua academia."
            ) {
                MultiPillGrid(
                    options = viewModel.equipamentosDisponiveis().map { it.valor to it.label },
                    selectedValues = form.equipamentos,
                    onToggle = { form.toggleEquipamento(it) }
                )
            }

            FormSection(title = "Restrições ou lesões (opcional)") {
                OutlinedTextField(
                    value = form.restricoes,
                    onValueChange = { form.restricoes = it },
                    placeholder = { Text("ex: dor no ombro direito, evitar agachamento livre") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = treinoTextFieldColors()
                )
            }

            TextButton(
                onClick = { viewModel.abrirConfig() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Configurar chave da API do Claude",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // CTA fixo na zona do polegar — padrão de apps fitness atuais
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
        ) {
            Button(
                onClick = { viewModel.gerarTreino() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
            ) {
                Text("GERAR TREINO DE HOJE", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun treinoTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
)
