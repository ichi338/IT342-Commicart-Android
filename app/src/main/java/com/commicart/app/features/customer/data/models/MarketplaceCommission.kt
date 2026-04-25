package com.commicart.app.features.customer.data.models

import com.commicart.app.features.profile.data.models.User

data class ArtistListing(
    val artist: User,
    val portfolioCount: Int = 0,
    val averagePrice: Double = 0.0,
    val description: String? = null,
    val isAvailable: Boolean = true
)

data class MarketplaceCommission(
    val id: String,
    val title: String,
    val description: String,
    val image: String? = null,
    val price: Double,
    val artistId: String,
    val artistName: String,
    val artistHandle: String? = null,
    val createdAt: String? = null
)
