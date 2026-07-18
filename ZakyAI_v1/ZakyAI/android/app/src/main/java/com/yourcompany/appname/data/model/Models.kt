package com.yourcompany.appname.data.model

/**
 * نموذج المستخدم - يُخزن في مجموعة "users" في Firestore
 */
data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val role: String = "user", // "user" أو "admin"
    val bio: String = "",       // النبذة الشخصية التي يقرأها النموذج
    val isGuest: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * رسالة واحدة داخل محادثة
 */
data class Message(
    val role: String = "user", // "user" أو "assistant"
    val text: String = "",
    val imageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * محادثة كاملة - تُخزن في مجموعة "conversations"
 */
data class Conversation(
    val id: String = "",
    val userId: String = "",
    val title: String = "محادثة جديدة",
    val messages: List<Message> = emptyList(),
    val model: String = "groq",
    val temperature: Float = 0.7f,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * تعريف نموذج ذكاء اصطناعي (يمكن إضافته/حذفه من لوحة الإدارة)
 */
data class AIModel(
    val id: String = "",
    val name: String = "",
    val apiUrl: String = "",
    val apiKeyRef: String = "", // اسم مرجعي للمفتاح، وليس المفتاح نفسه بنص صريح
    val type: String = "text", // "text" | "image" | "audio"
    val isActive: Boolean = true,
    val sortOrder: Int = 0,
    val isBuiltIn: Boolean = false
)

/**
 * إعدادات المستخدم المحلية (تُخزن في DataStore)
 */
data class UserSettings(
    val selectedModel: String = "groq",
    val temperature: Float = 0.7f,
    val userBio: String = "",
    val theme: String = "dark", // dark | light | neon | blue | purple
)

/** إحصائيات لوحة الإدارة */
data class AdminStats(
    val totalUsers: Int = 0,
    val totalConversations: Int = 0,
    val requestsToday: Int = 0
)
