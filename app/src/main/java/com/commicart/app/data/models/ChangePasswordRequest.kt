package com.commicart.app.data.models

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)