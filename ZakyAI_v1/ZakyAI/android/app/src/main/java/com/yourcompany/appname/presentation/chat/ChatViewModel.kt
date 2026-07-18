package com.yourcompany.appname.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.appname.data.model.Conversation
import com.yourcompany.appname.data.model.Message
import com.yourcompany.appname.data.repository.AiChatService
import com.yourcompany.appname.data.repository.AuthRepository
import com.yourcompany.appname.data.repository.ConversationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val conversation: Conversation = Conversation(),
    val isSending: Boolean = false,
    val errorMessage: String? = null,
    val provider: String = "groq",
    val modelName: String = "llama-3.3-70b-versatile",
    val temperature: Float = 0.7f,
    val userBio: String = ""
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val aiChatService: AiChatService,
    private val conversationRepository: ConversationRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun loadConversation(id: String) {
        viewModelScope.launch {
            val conv = conversationRepository.getConversation(id) ?: Conversation(
                userId = authRepository.currentUserId.orEmpty()
            )
            _uiState.update { it.copy(conversation = conv) }
        }
    }

    fun startNewConversation() {
        _uiState.update {
            it.copy(conversation = Conversation(userId = authRepository.currentUserId.orEmpty()))
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val state = _uiState.value
        val userMessage = Message(role = "user", text = text)
        val updatedConv = state.conversation.copy(messages = state.conversation.messages + userMessage)
        _uiState.update { it.copy(conversation = updatedConv, isSending = true, errorMessage = null) }

        viewModelScope.launch {
            conversationRepository.saveConversation(updatedConv)

            val result = aiChatService.sendMessage(
                provider = state.provider,
                modelName = state.modelName,
                history = updatedConv.messages,
                userBio = state.userBio,
                temperature = state.temperature
            )

            result.onSuccess { replyText ->
                val assistantMessage = Message(role = "assistant", text = replyText)
                val finalConv = updatedConv.copy(messages = updatedConv.messages + assistantMessage)
                conversationRepository.saveConversation(finalConv)
                _uiState.update { it.copy(conversation = finalConv, isSending = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isSending = false, errorMessage = e.message ?: "حدث خطأ غير متوقع") }
            }
        }
    }

    /** إعادة إنشاء آخر رد من المساعد */
    fun regenerateLastResponse() {
        val messages = _uiState.value.conversation.messages
        val lastUserMessage = messages.lastOrNull { it.role == "user" } ?: return
        val withoutLastAssistant = if (messages.lastOrNull()?.role == "assistant") messages.dropLast(1) else messages
        _uiState.update { it.copy(conversation = it.conversation.copy(messages = withoutLastAssistant)) }
        sendMessage(lastUserMessage.text)
    }

    fun generateImage(prompt: String): String = aiChatService.buildImageUrl(prompt)

    fun updateModelSelection(provider: String, modelName: String) {
        _uiState.update { it.copy(provider = provider, modelName = modelName) }
    }

    fun updateTemperature(value: Float) {
        _uiState.update { it.copy(temperature = value) }
    }

    fun updateUserBio(bio: String) {
        _uiState.update { it.copy(userBio = bio) }
    }
}
