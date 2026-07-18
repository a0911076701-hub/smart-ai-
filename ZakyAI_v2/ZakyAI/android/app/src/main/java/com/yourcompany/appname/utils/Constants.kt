package com.yourcompany.appname.utils

import com.yourcompany.appname.BuildConfig

/**
 * جميع الثوابت والمفاتيح.
 * ملاحظة أمنية: القيم الفعلية للمفاتيح لا تُكتب هنا مباشرة،
 * بل تُقرأ من BuildConfig التي تُولَّد من local.properties عند البناء.
 * هذا يمنع تسرّب المفاتيح في حال رفع الكود إلى Git.
 */
object Constants {

    // ---- Firestore Collections ----
    const val COLLECTION_USERS = "users"
    const val COLLECTION_CONVERSATIONS = "conversations"
    const val COLLECTION_MODELS = "ai_models"
    const val COLLECTION_ADMIN = "admin_config"
    const val COLLECTION_STATS = "stats"

    // ---- Groq ----
    val GROQ_API_KEY get() = BuildConfig.GROQ_API_KEY
    val GROQ_BASE_URL get() = BuildConfig.GROQ_BASE_URL
    const val GROQ_DEFAULT_MODEL = "llama-3.3-70b-versatile"

    // ---- Hugging Face ----
    val HF_API_KEY get() = BuildConfig.HUGGINGFACE_API_KEY
    val HF_BASE_URL get() = BuildConfig.HUGGINGFACE_BASE_URL

    // ---- Cerebras ----
    val CEREBRAS_API_KEY get() = BuildConfig.CEREBRAS_API_KEY
    val CEREBRAS_BASE_URL get() = BuildConfig.CEREBRAS_BASE_URL

    // ---- OpenRouter ----
    val OPENROUTER_API_KEY get() = BuildConfig.OPENROUTER_API_KEY
    val OPENROUTER_BASE_URL get() = BuildConfig.OPENROUTER_BASE_URL

    // ---- Pollinations (Image Gen) ----
    val POLLINATIONS_BASE_URL get() = BuildConfig.POLLINATIONS_BASE_URL

    // ---- Chat ----
    const val MAX_CONTEXT_MESSAGES = 20
    const val DEFAULT_TEMPERATURE = 0.7f

    // ---- DataStore keys ----
    const val DATASTORE_NAME = "zakyai_settings"
    const val KEY_SELECTED_MODEL = "selected_model"
    const val KEY_TEMPERATURE = "temperature"
    const val KEY_THEME = "theme"
    const val KEY_USER_BIO = "user_bio"

    // ---- Admin ----
    // كلمة مرور المدير الافتراضية للتطوير فقط. في الإنتاج، خزّن hash (SHA-256) في
    // مستند Firestore (admin_config/main -> passwordHash) وتحقق منه في AdminViewModel،
    // بدل مقارنة نص صريح هنا.
    const val ADMIN_PANEL_TRIGGER_TAPS = 5
}
