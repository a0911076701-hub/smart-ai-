package com.yourcompany.appname.data.repository

import com.yourcompany.appname.data.model.Message
import com.yourcompany.appname.data.remote.ChatCompletionRequest
import com.yourcompany.appname.data.remote.ChatMessageDto
import com.yourcompany.appname.data.remote.HuggingFaceApi
import com.yourcompany.appname.data.remote.HuggingFaceRequest
import com.yourcompany.appname.data.remote.OpenAiCompatibleApi
import com.yourcompany.appname.utils.Constants
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * نقطة الدخول الموحدة لإرسال رسالة إلى أي مزود ذكاء اصطناعي.
 * تُخفي هذه الطبقة الاختلافات بين Groq / Cerebras / OpenRouter / Hugging Face
 * عن بقية التطبيق، بحيث يتعامل الـ ViewModel مع دالة واحدة فقط.
 */
@Singleton
class AiChatService @Inject constructor(
    @Named("groq") private val groqApi: OpenAiCompatibleApi,
    @Named("cerebras") private val cerebrasApi: OpenAiCompatibleApi,
    @Named("openrouter") private val openRouterApi: OpenAiCompatibleApi,
    private val huggingFaceApi: HuggingFaceApi
) {
    /**
     * يرسل تاريخ المحادثة كاملاً (بما يضمن السياق) ويعيد رد النموذج كنص.
     * @param provider أحد القيم: "groq" | "cerebras" | "openrouter" | "huggingface"
     */
    suspend fun sendMessage(
        provider: String,
        modelName: String,
        history: List<Message>,
        userBio: String,
        temperature: Float
    ): Result<String> = runCatching {
        val systemPrompt = buildSystemPrompt(userBio)
        val dtoMessages = listOf(ChatMessageDto("system", systemPrompt)) +
            history.map { ChatMessageDto(if (it.role == "assistant") "assistant" else "user", it.text) }

        when (provider) {
            "groq" -> callOpenAiCompatible(groqApi, Constants.GROQ_API_KEY, modelName, dtoMessages, temperature)
            "cerebras" -> callOpenAiCompatible(cerebrasApi, Constants.CEREBRAS_API_KEY, modelName, dtoMessages, temperature)
            "openrouter" -> callOpenAiCompatible(openRouterApi, Constants.OPENROUTER_API_KEY, modelName, dtoMessages, temperature)
            "huggingface" -> callHuggingFace(modelName, history.lastOrNull()?.text.orEmpty(), temperature)
            else -> error("مزود غير معروف: $provider")
        }
    }

    private suspend fun callOpenAiCompatible(
        api: OpenAiCompatibleApi,
        apiKey: String,
        model: String,
        messages: List<ChatMessageDto>,
        temperature: Float
    ): String {
        require(apiKey.isNotBlank()) { "مفتاح API غير موجود. تحقق من local.properties." }
        val response = api.chatCompletion(
            authHeader = "Bearer $apiKey",
            request = ChatCompletionRequest(model = model, messages = messages, temperature = temperature)
        )
        return response.choices.firstOrNull()?.message?.content
            ?: "لم يصل رد من النموذج، حاول مرة أخرى."
    }

    private suspend fun callHuggingFace(model: String, prompt: String, temperature: Float): String {
        require(Constants.HUGGINGFACE_API_KEY.isNotBlank()) { "مفتاح Hugging Face غير موجود." }
        val url = Constants.HUGGINGFACE_BASE_URL + model
        val response = huggingFaceApi.generate(
            modelUrl = url,
            authHeader = "Bearer ${Constants.HUGGINGFACE_API_KEY}",
            request = HuggingFaceRequest(inputs = prompt, parameters = mapOf(
                "temperature" to temperature,
                "max_new_tokens" to 512
            ))
        )
        return response.firstOrNull()?.generated_text ?: "لم يصل رد من النموذج."
    }

    private fun buildSystemPrompt(userBio: String): String = buildString {
        append("أنت ZakyAI، مساعد ذكي ودود يتحدث العربية بطلاقة. ")
        if (userBio.isNotBlank()) {
            append("معلومات عن المستخدم لتخصيص ردودك: $userBio. ")
        }
        append("كن مختصرًا ومفيدًا وصادقًا في إجاباتك.")
    }

    /** إنشاء صورة عبر Pollinations.ai - لا يحتاج مفتاح API */
    fun buildImageUrl(prompt: String): String {
        val encoded = java.net.URLEncoder.encode(prompt, "UTF-8")
        return "${Constants.POLLINATIONS_BASE_URL}$encoded"
    }
}
