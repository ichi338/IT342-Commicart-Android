package com.commicart.app.features.auth.data.models

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val role: String
)
