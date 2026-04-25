package com.commicart.app.features.customer.domain.contracts

import com.commicart.app.features.customer.data.models.CommissionRequest

interface CommissionRequestContract {
    interface View {
        fun showProgress()
        fun hideProgress()
        fun displayArtistInfo(artistId: String, artistName: String)
        fun showRequestSubmitted(request: CommissionRequest)
        fun showRequestSubmittedMessage(message: String)
        fun showError(message: String)
        fun navigateBack()
        fun navigateToMarketplace()
        fun navigateToLogin()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadArtistInfo(artistId: String)
        fun submitCommissionRequest(
            title: String,
            description: String,
            artistId: String
        )
        fun onBackClick()
    }
}
