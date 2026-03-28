package com.commicart.app.data.models

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val role: String
)
