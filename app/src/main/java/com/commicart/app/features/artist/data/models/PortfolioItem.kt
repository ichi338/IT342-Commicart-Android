package com.commicart.app.features.artist.data.models

data class PortfolioItem(
    val id: String,
    val title: String,
    val description: String,
    val image: String? = null,
    val price: Double,
    val artistId: String,
    val commission: Commission? = null,
    val isAvailable: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class Portfolio(
    val artistId: String,
    val items: List<PortfolioItem> = emptyList(),
    val totalItems: Int = 0,
    val totalEarnings: Double = 0.0,
    val averageRating: Double = 0.0
)
