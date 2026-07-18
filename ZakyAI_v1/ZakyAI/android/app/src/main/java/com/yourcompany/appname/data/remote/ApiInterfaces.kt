package com.yourcompany.appname.data.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * طلبات الدردشة (Chat Completion) لـ Groq / Cerebras / OpenRouter تتبع جميعها
 * صيغة OpenAI المتوافقة، لذا نستخدم واجهة Retrofit موحدة لها.
 */

data class ChatMessageDto(
    val role: String,
    val content: String
)

data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessageDto>,
    val temperature: Float = 0.7f,
    val max_tokens: Int = 2048,
    val stream: Boolean = false
)

data class ChatCompletionChoice(
    val message: ChatMessageDto
)

data class ChatCompletionResponse(
    val choices: List<ChatCompletionChoice>
)

/** واجهة موحدة تُستخدم من Groq / Cerebras / OpenRouter (صيغة OpenAI-compatible) */
interface OpenAiCompatibleApi {
    @POST("chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") authHeader: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}

/** طلب Hugging Face Inference API (صيغة مختلفة عن OpenAI) */
data class HuggingFaceRequest(
    val inputs: String,
    val parameters: Map<String, @JvmSuppressWildcards Any> = mapOf(
        "temperature" to 0.7,
        "max_new_tokens" to 512
    )
)

data class HuggingFaceResponseItem(
    val generated_text: String
)

interface HuggingFaceApi {
    @POST
    suspend fun generate(
        @Url modelUrl: String,
        @Header("Authorization") authHeader: String,
        @Body request: HuggingFaceRequest
    ): List<HuggingFaceResponseItem>
}
