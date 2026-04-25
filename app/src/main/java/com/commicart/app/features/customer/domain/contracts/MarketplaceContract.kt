package com.commicart.app.features.customer.domain.contracts

import com.commicart.app.features.customer.data.models.MarketplaceCommission
import com.commicart.app.features.customer.data.models.ArtistListing

interface MarketplaceContract {
    interface View {
        fun showProgress()
        fun hideProgress()
        fun displayCommissions(commissions: List<MarketplaceCommission>)
        fun displayTopArtists(artists: List<ArtistListing>)
        fun displaySearchResults(commissions: List<MarketplaceCommission>)
        fun showCommissionsEmpty()
        fun showSearchEmpty()
        fun showError(message: String)
        fun navigateToCommissionDetails(commissionId: String)
        fun navigateToArtistProfile(artistId: String)
        fun navigateToCommissionRequest(artistId: String)
        fun navigateToLogin()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadAvailableCommissions()
        fun loadTopArtists()
        fun searchCommissions(query: String)
        fun filterCommissions(filterBy: String)
        fun onCommissionClick(commissionId: String)
        fun onArtistClick(artistId: String)
        fun onRequestCommissionClick(artistId: String)
        fun onSearchQueryChanged(query: String)
    }
}
