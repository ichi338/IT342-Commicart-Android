package com.commicart.app.data.models

data class EditProfileRequest(
    val fullName: String,
    val bio: String? = null,
    val phone: String? = null,
    val email: String? = null
)
