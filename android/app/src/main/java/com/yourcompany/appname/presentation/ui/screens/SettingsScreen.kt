package com.yourcompany.appname.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yourcompany.appname.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    onOpenAdmin: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val models = listOf("groq", "huggingface", "cerebras", "openrouter")
    val themes = listOf("dark", "light", "neon", "blue", "purple")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("الإعدادات", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        Text("النموذج", style = MaterialTheme.typography.titleMedium)
        Row {
            models.forEach { model ->
                FilterChip(
                    selected = state.selectedModel == model,
                    onClick = { viewModel.setModel(model) },
                    label = { Text(model) },
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("درجة الحرارة: ${"%.1f".format(state.temperature)}")
        Slider(value = state.temperature, onValueChange = { viewModel.setTemperature(it) }, valueRange = 0f..2f)

        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = state.bio, onValueChange = { viewModel.setBio(it) }, label = { Text("نبذة عنك") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(16.dp))
        Text("الثيم", style = MaterialTheme.typography.titleMedium)
        Row {
            themes.forEach { theme ->
                FilterChip(
                    selected = state.theme == theme,
                    onClick = { viewModel.setTheme(theme) },
                    label = { Text(theme) },
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        OutlinedButton(onClick = { viewModel.resetSettings() }) { Text("إعادة تعيين الإعدادات") }

        Spacer(Modifier.weight(1f))
        Text("الإصدار 1.0.0", modifier = Modifier.clickable { if (viewModel.onVersionTapped()) onOpenAdmin() }, style = MaterialTheme.typography.bodySmall)
    }
}
