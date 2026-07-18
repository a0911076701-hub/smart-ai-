package com.yourcompany.appname.data.model

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
