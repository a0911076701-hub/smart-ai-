package com.yourcompany.appname.data.model

data class Message(
    val role: String = "user", // "user" أو "assistant"
    val text: String = "",
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
