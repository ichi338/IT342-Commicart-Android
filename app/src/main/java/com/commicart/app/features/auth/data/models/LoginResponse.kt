package com.commicart.app.features.auth.data.models

data class LoginResponse(
    val token: String,
    val userId: String,
    val email: String,
    val fullName: String,
    val role: String
)