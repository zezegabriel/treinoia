package com.treinoia.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.treinoia.app.viewmodel.WorkoutViewModel

@Composable
fun ConfigScreen(viewModel: WorkoutViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Chave da API do Claude", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = viewModel.apiKeyInput,
            onValueChange = { viewModel.apiKeyInput = it },
            placeholder = { Text("Cole sua API key aqui (sk-ant-...)") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = treinoTextFieldColors()
        )

        Text(
            "Gere sua chave em console.anthropic.com/settings/keys. A chave fica salva " +
                "apenas neste dispositivo, nunca é enviada para nenhum servidor além da Anthropic.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Button(
            onClick = { viewModel.salvarApiKey() },
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Salvar chave")
        }

        OutlinedButton(
            onClick = { viewModel.voltarParaForm() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text("Voltar")
        }
    }
}
