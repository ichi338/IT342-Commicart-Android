package com.commicart.app.features.profile.data.models

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)