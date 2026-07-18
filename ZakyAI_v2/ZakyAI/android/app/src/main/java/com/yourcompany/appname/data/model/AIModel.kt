package com.yourcompany.appname.data.model

/** يمثل نموذج ذكاء اصطناعي مُضافًا من لوحة الإدارة */
data class AIModel(
    val id: String = "",
    val name: String = "",
    val apiUrl: String = "",
    // لا يُخزَّن مفتاح الـ API الفعلي هنا في العميل مطلقاً لنماذج مضافة من لوحة الإدارة؛
    // بدلاً من ذلك يُخزَّن بشكل مشفّر من طرف الخادم أو في Firestore مع قواعد أمان صارمة
    // تمنع أي مستخدم غير مدير من قراءته. راجع server/app.py للتوسّط الآمن.
    val apiKeyRef: String = "", // مرجع/معرّف المفتاح فقط، وليس المفتاح نفسه
    val type: String = "text", // "text" | "image" | "audio"
    val isActive: Boolean = true,
    val order: Int = 0,
    val isDefault: Boolean = false
)
