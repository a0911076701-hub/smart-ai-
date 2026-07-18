package com.yourcompany.appname.utils

/**
 * الثوابت العامة للتطبيق.
 *
 * ⚠️ ملاحظة أمنية مهمة:
 * لا تضع أي مفتاح API حقيقي هنا مباشرة في الكود المصدري.
 * جميع المفاتيح يجب أن تُقرأ من BuildConfig، والتي بدورها تُقرأ من ملف
 * local.properties (غير المرفوع إلى Git عبر .gitignore).
 *
 * شاهد ملف android/local.properties.example لمعرفة الصيغة المطلوبة،
 * وملف app/build.gradle.kts لمعرفة كيف يتم تمرير هذه القيم إلى BuildConfig.
 */
object Constants {

    // ---------- Firebase ----------
    // يتم تحميل تهيئة Firebase تلقائيًا من google-services.json
    // (لا حاجة لوضع مفاتيح Firebase هنا).

    // ---------- روابط APIs (الروابط ثابتة وليست سرية) ----------
    const val GROQ_BASE_URL = "https://api.groq.com/openai/v1/"
    const val HUGGINGFACE_BASE_URL = "https://api-inference.huggingface.co/models/"
    const val CEREBRAS_BASE_URL = "https://api.cerebras.ai/v1/"
    const val OPENROUTER_BASE_URL = "https://openrouter.ai/api/v1/"
    const val POLLINATIONS_BASE_URL = "https://image.pollinations.ai/prompt/"

    // ---------- مفاتيح APIs (تُقرأ من BuildConfig - انظر الملاحظة أعلاه) ----------
    val GROQ_API_KEY: String get() = BuildConfigKeys.GROQ_API_KEY
    val HUGGINGFACE_API_KEY: String get() = BuildConfigKeys.HUGGINGFACE_API_KEY
    val CEREBRAS_API_KEY: String get() = BuildConfigKeys.CEREBRAS_API_KEY
    val OPENROUTER_API_KEY: String get() = BuildConfigKeys.OPENROUTER_API_KEY

    // ---------- إعدادات النماذج الافتراضية ----------
    const val DEFAULT_GROQ_MODEL = "llama-3.3-70b-versatile"
    const val DEFAULT_TEMPERATURE = 0.7f
    const val MAX_CONTEXT_MESSAGES = 20

    // ---------- Firestore Collections ----------
    const val COLLECTION_USERS = "users"
    const val COLLECTION_CONVERSATIONS = "conversations"
    const val COLLECTION_MODELS = "ai_models"
    const val COLLECTION_ADMIN_CONFIG = "admin_config"

    // ---------- DataStore Keys ----------
    const val DATASTORE_NAME = "zakyai_settings"

    // ---------- لوحة الإدارة ----------
    // ⚠️ كلمة مرور المدير الافتراضية لأول تشغيل فقط.
    // يجب تغييرها فورًا من داخل لوحة الإدارة بعد أول دخول - لا تتركها كما هي.
    // يتم تخزين الهاش (hash) الخاص بها في Firestore بعد أول تغيير، وليس كنص صريح.
    const val ADMIN_DEFAULT_PASSWORD_HASH_PLACEHOLDER = "CHANGE_ME_ON_FIRST_LOGIN"
    const val ADMIN_LOGO_TAPS_TO_OPEN = 2
    const val VERSION_TAPS_TO_OPEN_ADMIN = 5
}

/**
 * تُملأ هذه القيم تلقائيًا من BuildConfig التي تُنشأ من local.properties.
 * هذا الملف يعمل كواجهة وسيطة حتى يسهل استبداله لاحقًا (مثلاً بقراءة من
 * Firebase Remote Config بدلاً من BuildConfig).
 */
object BuildConfigKeys {
    val GROQ_API_KEY: String
        get() = try {
            com.yourcompany.appname.BuildConfig.GROQ_API_KEY
        } catch (e: Exception) { "" }

    val HUGGINGFACE_API_KEY: String
        get() = try {
            com.yourcompany.appname.BuildConfig.HUGGINGFACE_API_KEY
        } catch (e: Exception) { "" }

    val CEREBRAS_API_KEY: String
        get() = try {
            com.yourcompany.appname.BuildConfig.CEREBRAS_API_KEY
        } catch (e: Exception) { "" }

    val OPENROUTER_API_KEY: String
        get() = try {
            com.yourcompany.appname.BuildConfig.OPENROUTER_API_KEY
        } catch (e: Exception) { "" }
}
