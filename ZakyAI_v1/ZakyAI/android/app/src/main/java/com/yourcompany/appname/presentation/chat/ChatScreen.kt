package com.yourcompany.appname.presentation.chat

import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yourcompany.appname.data.model.Message

/**
 * شاشة الدردشة الرئيسية: قائمة رسائل + شريط إدخال (نص/صوت/صورة).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onOpenDrawer: () -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val clipboard = LocalClipboardManager.current

    val speechLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val spoken = result.data
            ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            ?.firstOrNull()
        if (!spoken.isNullOrBlank()) inputText = spoken
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ZakyAI") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) { Text("☰") }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) { Text("⚙") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFF0D0D0D))
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.conversation.messages) { message ->
                    MessageBubble(
                        message = message,
                        onCopy = { clipboard.setText(AnnotatedString(message.text)) },
                        onRegenerate = { viewModel.regenerateLastResponse() }
                    )
                }
                if (uiState.isSending) {
                    item { TypingIndicator() }
                }
            }

            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color(0xFFFF6B6B),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            ChatInputBar(
                text = inputText,
                onTextChange = { inputText = it },
                onSend = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    }
                },
                onMicClick = {
                    val intent = android.content.Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-SA")
                    }
                    speechLauncher.launch(intent)
                },
                onImageClick = {
                    if (inputText.isNotBlank()) {
                        val url = viewModel.generateImage(inputText)
                        viewModel.sendMessage("🖼 صورة: $inputText\n$url")
                        inputText = ""
                    }
                }
            )
        }
    }
}

@Composable
private fun MessageBubble(message: Message, onCopy: () -> Unit, onRegenerate: () -> Unit) {
    val isUser = message.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = if (isUser) Color(0xFF2B5CE6) else Color(0xFF1A1A1A),
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(12.dp)
                .widthIn(max = 300.dp)
        ) {
            Text(text = message.text, color = Color.White)
            if (!isUser) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(onClick = onCopy) { Text("نسخ", fontSize = androidx.compose.ui.unit.TextUnit.Unspecified) }
                    TextButton(onClick = onRegenerate) { Text("إعادة إنشاء") }
                }
            }
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(modifier = Modifier.padding(start = 12.dp)) {
        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
        Spacer(modifier = Modifier.width(8.dp))
        Text("ZakyAI يكتب...", color = Color.Gray)
    }
}

@Composable
private fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onMicClick: () -> Unit,
    onImageClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMicClick) { Icon(Icons.Default.Mic, contentDescription = "إدخال صوتي") }
        IconButton(onClick = onImageClick) { Icon(Icons.Default.Image, contentDescription = "إنشاء صورة") }
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("اكتب رسالتك...") }
        )
        IconButton(onClick = onSend) { Icon(Icons.Default.Send, contentDescription = "إرسال") }
    }
}
