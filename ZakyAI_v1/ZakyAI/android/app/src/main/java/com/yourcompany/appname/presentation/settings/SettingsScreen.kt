package com.yourcompany.appname.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

private val availableModels = listOf(
    "groq" to "Groq (llama-3.3-70b)",
    "cerebras" to "Cerebras",
    "openrouter" to "OpenRouter",
    "huggingface" to "Hugging Face"
)
private val availableThemes = listOf("dark", "light", "neon", "blue", "purple")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onOpenAdmin: () -> Unit,
    onBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val adminUnlocked by viewModel.adminUnlocked.collectAsState()

    LaunchedEffect(adminUnlocked) {
        if (adminUnlocked) onOpenAdmin()
    }

    Scaffold(topBar = { TopAppBar(title = { Text("الإعدادات") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            Text("النموذج المستخدم", style = MaterialTheme.typography.titleMedium)
            availableModels.forEach { (id, label) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = settings.selectedModel == id,
                        onClick = { viewModel.updateModel(id) }
                    )
                    Text(label)
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("درجة الحرارة (الإبداع): ${"%.1f".format(settings.temperature)}")
            Slider(
                value = settings.temperature,
                onValueChange = { viewModel.updateTemperature(it) },
                valueRange = 0f..2f
            )

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = settings.userBio,
                onValueChange = { viewModel.updateBio(it) },
                label = { Text("نبذة عنك (يستخدمها المساعد لتخصيص الردود)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(Modifier.height(16.dp))
            Text("الثيم", style = MaterialTheme.typography.titleMedium)
            Row {
                availableThemes.forEach { theme ->
                    FilterChip(
                        selected = settings.theme == theme,
                        onClick = { viewModel.updateTheme(theme) },
                        label = { Text(theme) },
                        modifier = Modifier.padding(end = 6.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = { viewModel.resetSettings() }) {
                Text("إعادة تعيين الإعدادات")
            }

            Spacer(Modifier.weight(1f))
            // الضغط 5 مرات على رقم الإصدار يفتح لوحة الإدارة
            Text(
                text = "الإصدار 1.0.0",
                modifier = Modifier.clickable { viewModel.onVersionTapped() },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
