package com.commicart.app.features.profile.data.models

data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val bio: String? = null,
    val phone: String? = null,
    val profilePictureUrl: String? = null,
    val role: String = "CUSTOMER",
    val createdAt: String? = null,
    val updatedAt: String? = null
)