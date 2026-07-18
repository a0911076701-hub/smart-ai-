package com.yourcompany.appname.data.model

data class AIModel(
    val id: String = "",
    val name: String = "",
    val apiUrl: String = "",
    val apiKeyRef: String = "",
    val type: String = "text",
    val isActive: Boolean = true,
    val order: Int = 0,
    val isDefault: Boolean = false
)
