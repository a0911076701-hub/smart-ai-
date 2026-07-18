package com.yourcompany.appname.data.model

data class Message(
    val role: String = "user",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
