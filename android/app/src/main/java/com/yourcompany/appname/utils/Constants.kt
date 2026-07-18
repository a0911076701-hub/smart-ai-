package com.yourcompany.appname.utils
import com.yourcompany.appname.BuildConfig

object Constants {
    const val COLLECTION_USERS = "users"
    const val COLLECTION_CONVERSATIONS = "conversations"
    const val COLLECTION_MODELS = "ai_models"
    const val COLLECTION_ADMIN = "admin_config"
    const val MAX_CONTEXT_MESSAGES = 20
    const val DEFAULT_TEMPERATURE = 0.7f
    const val DATASTORE_NAME = "zakyai_settings"
    const val KEY_SELECTED_MODEL = "selected_model"
    const val KEY_TEMPERATURE = "temperature"
    const val KEY_THEME = "theme"
    const val KEY_USER_BIO = "user_bio"
    const val ADMIN_PANEL_TRIGGER_TAPS = 5
    
    val GROQ_API_KEY get() = BuildConfig.GROQ_API_KEY
    val GROQ_BASE_URL get() = BuildConfig.GROQ_BASE_URL
    val HF_API_KEY get() = BuildConfig.HUGGINGFACE_API_KEY
    val CEREBRAS_API_KEY get() = BuildConfig.CEREBRAS_API_KEY
    val OPENROUTER_API_KEY get() = BuildConfig.OPENROUTER_API_KEY
}
