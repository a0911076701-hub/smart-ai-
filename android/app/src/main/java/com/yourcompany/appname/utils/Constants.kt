package com.yourcompany.appname.utils

import com.yourcompany.appname.BuildConfig

object Constants {

    // ---- Firestore Collections ----
    const val COLLECTION_USERS = "users"
    const val COLLECTION_CONVERSATIONS = "conversations"
    const val COLLECTION_MODELS = "ai_models"
    const val COLLECTION_ADMIN = "admin_config"

    // ---- Groq ----
    val GROQ_API_KEY get() = BuildConfig.GROQ_API_KEY
    const val GROQ_DEFAULT_MODEL = "llama-3.3-70b-versatile"

    // ---- Hugging Face ----
    val HF_API_KEY get() = BuildConfig.HUGGINGFACE_API_KEY

    // ---- Cerebras ----
    val CEREBRAS_API_KEY get() = BuildConfig.CEREBRAS_API_KEY

    // ---- OpenRouter ----
    val OPENROUTER_API_KEY get() = BuildConfig.OPENROUTER_API_KEY

    // ---- Chat ----
    const val MAX_CONTEXT_MESSAGES = 20
    const val DEFAULT_TEMPERATURE = 0.7f

    // ---- Admin ----
    const val ADMIN_PANEL_TRIGGER_TAPS = 5
}
