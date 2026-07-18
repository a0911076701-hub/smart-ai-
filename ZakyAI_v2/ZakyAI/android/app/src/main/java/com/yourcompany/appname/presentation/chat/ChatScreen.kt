package com.yourcompany.appname.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yourcompany.appname.data.model.Message

/** شاشة المحادثة الرئيسية */
@Composable
fun ChatScreen(
    userId: String,
    onOpenSettings: () -> Unit,
    onOpenDrawer: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var input by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ZakyAI") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "القائمة")
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "الإعدادات")
                    }
                }
            )
        },
        bottomBar = {
            ChatInputBar(
                value = input,
                onValueChange = { input = it },
                onSend = {
                    viewModel.sendMessage(userId, input)
                    input = ""
                },
                onVoiceClick = { /* TODO: تشغيل SpeechRecognizer من MainActivity وتمرير النص هنا */ },
                onImageGenClick = { viewModel.sendMessage(userId, "أنشئ صورة: $input") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.messages) { message ->
                MessageBubble(
                    message = message,
                    onCopy = { /* TODO: ClipboardManager */ },
                    onSpeak = { /* TODO: TextToSpeech */ },
                    onRegenerate = { viewModel.regenerateLast(userId) }
                )
            }
            if (uiState.isTyping) {
                item { TypingIndicator() }
            }
            uiState.error?.let {
                item { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp)) }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: Message,
    onCopy: () -> Unit,
    onSpeak: () -> Unit,
    onRegenerate: () -> Unit
) {
    val isUser = message.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isUser) Color(0xFF3D5AFE) else Color(0xFF1A1A1A))
                .padding(12.dp)
                .fillMaxWidth(0.8f)
        ) {
            Text(message.text, color = Color.White)
            if (!isUser) {
                Row(modifier = Modifier.padding(top = 6.dp)) {
                    IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = Color.Gray)
                    }
                    IconButton(onClick = onSpeak, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "استماع", tint = Color.Gray)
                    }
                    IconButton(onClick = onRegenerate, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Refresh, contentDescription = "إعادة إنشاء", tint = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
        Spacer(Modifier.width(8.dp))
        Text("يكتب...", color = Color.Gray)
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onVoiceClick: () -> Unit,
    onImageGenClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onVoiceClick) {
            Icon(Icons.Default.Mic, contentDescription = "إدخال صوتي")
        }
        IconButton(onClick = onImageGenClick) {
            Icon(Icons.Default.Image, contentDescription = "إنشاء صورة")
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("اكتب رسالتك...") }
        )
        IconButton(onClick = onSend) {
            Icon(Icons.Default.Send, contentDescription = "إرسال")
        }
    }
}
