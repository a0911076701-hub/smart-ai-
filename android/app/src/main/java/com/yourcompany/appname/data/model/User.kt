package com.yourcompany.appname.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val role: String = "user",
    val isGuest: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
