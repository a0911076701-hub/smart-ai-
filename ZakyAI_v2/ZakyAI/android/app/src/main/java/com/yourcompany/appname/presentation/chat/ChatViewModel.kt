package com.yourcompany.appname.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.appname.data.model.Conversation
import com.yourcompany.appname.data.model.Message
import com.yourcompany.appname.data.remote.*
import com.yourcompany.appname.data.repository.ConversationRepository
import com.yourcompany.appname.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val isTyping: Boolean = false,
    val selectedModel: String = "groq",
    val temperature: Float = Constants.DEFAULT_TEMPERATURE,
    val error: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val groqApi: GroqApi,
    private val cerebrasApi: CerebrasApi,
    private val openRouterApi: OpenRouterApi,
    private val huggingFaceApi: HuggingFaceApi,
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var currentConversation: Conversation = Conversation()

    fun sendMessage(userId: String, text: String) {
        if (text.isBlank()) return
        val userMessage = Message(role = "user", text = text)
        val updated = (_uiState.value.messages + userMessage).takeLast(Constants.MAX_CONTEXT_MESSAGES)
        _uiState.value = _uiState.value.copy(messages = updated, isTyping = true, error = null)

        viewModelScope.launch {
            try {
                val reply = when (_uiState.value.selectedModel) {
                    "groq" -> callOpenAiStyle { groqApi.chatCompletion(it) }
                    "cerebras" -> callOpenAiStyle { cerebrasApi.chatCompletion(it) }
                    "openrouter" -> callOpenAiStyle { openRouterApi.chatCompletion(it) }
                    "huggingface" -> callHuggingFace(text)
                    else -> callOpenAiStyle { groqApi.chatCompletion(it) }
                }
                val assistantMessage = Message(role = "assistant", text = reply)
                val newMessages = (_uiState.value.messages + assistantMessage)
                _uiState.value = _uiState.value.copy(messages = newMessages, isTyping = false)

                currentConversation = currentConversation.copy(
                    userId = userId,
                    messages = newMessages,
                    model = _uiState.value.selectedModel,
                    temperature = _uiState.value.temperature,
                    updatedAt = System.currentTimeMillis()
                )
                conversationRepository.saveConversation(currentConversation)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isTyping = false, error = e.message ?: "حدث خطأ في الاتصال")
            }
        }
    }

    private suspend fun callOpenAiStyle(
        call: suspend (ChatCompletionRequest) -> ChatCompletionResponse
    ): String {
        val request = ChatCompletionRequest(
            model = Constants.GROQ_DEFAULT_MODEL,
            messages = _uiState.value.messages.map { ChatMessageDto(it.role, it.text) },
            temperature = _uiState.value.temperature
        )
        val response = call(request)
        return response.choices.firstOrNull()?.message?.content ?: "لم يصل رد من النموذج."
    }

    private suspend fun callHuggingFace(prompt: String): String {
        val modelUrl = "" // ضع مسار النموذج المطلوب من Hugging Face هنا (يُدار من لوحة الإدارة)
        val response = huggingFaceApi.generate(modelUrl, HFRequest(inputs = prompt))
        return response.firstOrNull()?.generated_text ?: "لم يصل رد من النموذج."
    }

    fun regenerateLast(userId: String) {
        val lastUserMessage = _uiState.value.messages.lastOrNull { it.role == "user" } ?: return
        val trimmed = _uiState.value.messages.dropLast(1)
        _uiState.value = _uiState.value.copy(messages = trimmed)
        sendMessage(userId, lastUserMessage.text)
    }

    fun selectModel(model: String) {
        _uiState.value = _uiState.value.copy(selectedModel = model)
    }

    fun setTemperature(value: Float) {
        _uiState.value = _uiState.value.copy(temperature = value)
    }

    fun loadConversation(conversation: Conversation) {
        currentConversation = conversation
        _uiState.value = _uiState.value.copy(
            messages = conversation.messages,
            selectedModel = conversation.model,
            temperature = conversation.temperature
        )
    }

    fun newConversation() {
        currentConversation = Conversation()
        _uiState.value = ChatUiState(selectedModel = _uiState.value.selectedModel, temperature = _uiState.value.temperature)
    }
}
