package com.yourcompany.appname.data.remote

// نماذج طلب/استجابة متوافقة مع OpenAI-style API (Groq, Cerebras, OpenRouter تستخدم نفس الشكل)

data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessageDto>,
    val temperature: Float = 0.7f,
    val stream: Boolean = false
)

data class ChatMessageDto(
    val role: String,
    val content: String
)

data class ChatCompletionResponse(
    val id: String? = null,
    val choices: List<Choice> = emptyList()
) {
    data class Choice(
        val index: Int = 0,
        val message: ChatMessageDto? = null
    )
}

// Hugging Face Inference API (شكل مبسّط للنماذج النصية)
data class HFRequest(
    val inputs: String,
    val parameters: HFParameters = HFParameters()
)

data class HFParameters(
    val temperature: Float = 0.7f,
    val max_new_tokens: Int = 512
)

data class HFResponseItem(
    val generated_text: String? = null
)
