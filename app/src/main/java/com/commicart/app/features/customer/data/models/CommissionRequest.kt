package com.commicart.app.features.customer.data.models

data class CommissionRequest(
    val id: String,
    val title: String,
    val description: String,
    val requesterName: String,
    val requesterEmail: String,
    val artistId: String,
    val status: String = "PENDING", // PENDING, ACCEPTED, REJECTED
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class SubmitCommissionRequest(
    val title: String,
    val description: String,
    val requesterEmail: String,
    val requesterName: String,
    val artistId: String
)
