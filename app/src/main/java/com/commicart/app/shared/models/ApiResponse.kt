package com.commicart.app.shared.models

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)
