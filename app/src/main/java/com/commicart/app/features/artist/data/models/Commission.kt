package com.commicart.app.features.artist.data.models

data class Commission(
    val id: String,
    val title: String,
    val description: String,
    val image: String? = null,
    val price: Double,
    val status: String, // "PENDING", "APPROVED", "IN_PROGRESS", "COMPLETED", "CANCELLED"
    val clientId: String? = null,
    val clientName: String? = null,
    val clientEmail: String? = null,
    val date: String? = null,
    val dueDate: String? = null,
    val artistId: String,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

enum class CommissionStatus {
    FOR_APPROVAL,
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
