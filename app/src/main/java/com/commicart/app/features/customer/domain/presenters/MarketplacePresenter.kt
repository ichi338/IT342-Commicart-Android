package com.commicart.app.features.customer.domain.presenters

import android.content.Context
import com.commicart.app.features.customer.domain.contracts.MarketplaceContract
import com.commicart.app.features.customer.data.repository.MarketplaceRepository
import com.commicart.app.core.utils.NetworkUtils

class MarketplacePresenter(
    private val context: Context,
    private val marketplaceRepository: MarketplaceRepository
) : MarketplaceContract.Presenter {

    private var view: MarketplaceContract.View? = null

    override fun attachView(view: MarketplaceContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadAvailableCommissions() {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            view?.showError("No internet connection")
            return
        }

        view?.showProgress()

        marketplaceRepository.getAvailableCommissions(
            null,
            null,
            object : MarketplaceRepository.MarketplaceCallback {
                override fun onSuccess(data: List<com.commicart.app.features.customer.data.models.MarketplaceCommission>) {
                    view?.hideProgress()
                    if (data.isEmpty()) {
                        view?.showCommissionsEmpty()
                    } else {
                        view?.displayCommissions(data)
                    }
                }

                override fun onError(message: String) {
                    view?.hideProgress()
                    view?.showError(message)
                    if (message.contains("authenticated") || message.contains("401")) {
                        view?.navigateToLogin()
                    }
                }
            }
        )
    }

    override fun loadTopArtists() {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            view?.showError("No internet connection")
            return
        }

        view?.showProgress()

        marketplaceRepository.getTopArtists(10, object : MarketplaceRepository.ArtistListCallback {
            override fun onSuccess(artists: List<com.commicart.app.features.customer.data.models.ArtistListing>) {
                view?.hideProgress()
                view?.displayTopArtists(artists)
            }

            override fun onError(message: String) {
                view?.hideProgress()
                view?.showError(message)
            }
        })
    }

    override fun searchCommissions(query: String) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            view?.showError("No internet connection")
            return
        }

        if (query.isEmpty()) {
            view?.showSearchEmpty()
            return
        }

        view?.showProgress()

        marketplaceRepository.getAvailableCommissions(
            query,
            null,
            object : MarketplaceRepository.MarketplaceCallback {
                override fun onSuccess(data: List<com.commicart.app.features.customer.data.models.MarketplaceCommission>) {
                    view?.hideProgress()
                    if (data.isEmpty()) {
                        view?.showSearchEmpty()
                    } else {
                        view?.displaySearchResults(data)
                    }
                }

                override fun onError(message: String) {
                    view?.hideProgress()
                    view?.showError(message)
                }
            }
        )
    }

    override fun filterCommissions(filterBy: String) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            view?.showError("No internet connection")
            return
        }

        view?.showProgress()

        marketplaceRepository.getAvailableCommissions(
            null,
            filterBy,
            object : MarketplaceRepository.MarketplaceCallback {
                override fun onSuccess(data: List<com.commicart.app.features.customer.data.models.MarketplaceCommission>) {
                    view?.hideProgress()
                    if (data.isEmpty()) {
                        view?.showCommissionsEmpty()
                    } else {
                        view?.displayCommissions(data)
                    }
                }

                override fun onError(message: String) {
                    view?.hideProgress()
                    view?.showError(message)
                }
            }
        )
    }

    override fun onCommissionClick(commissionId: String) {
        view?.navigateToCommissionDetails(commissionId)
    }

    override fun onArtistClick(artistId: String) {
        view?.navigateToArtistProfile(artistId)
    }

    override fun onRequestCommissionClick(artistId: String) {
        view?.navigateToCommissionRequest(artistId)
    }

    override fun onSearchQueryChanged(query: String) {
        if (query.isNotEmpty()) {
            searchCommissions(query)
        } else {
            loadAvailableCommissions()
        }
    }
}
