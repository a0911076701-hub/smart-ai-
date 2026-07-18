package com.yourcompany.appname.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val role: String = "user", // "user" أو "admin"
    val isGuest: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
