package com.commicart.app.features.customer.domain.presenters

import android.content.Context
import com.commicart.app.features.customer.domain.contracts.CommissionRequestContract
import com.commicart.app.features.customer.data.repository.MarketplaceRepository
import com.commicart.app.features.customer.data.models.SubmitCommissionRequest
import com.commicart.app.core.utils.NetworkUtils
import com.commicart.app.features.auth.data.repository.UserRepository

class CommissionRequestPresenter(
    private val context: Context,
    private val marketplaceRepository: MarketplaceRepository,
    private val userRepository: UserRepository
) : CommissionRequestContract.Presenter {

    private var view: CommissionRequestContract.View? = null
    private var currentArtistId: String? = null

    override fun attachView(view: CommissionRequestContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadArtistInfo(artistId: String) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            view?.showError("No internet connection")
            return
        }

        currentArtistId = artistId
        view?.showProgress()

        marketplaceRepository.getArtistDetails(
            artistId,
            { artist ->
                view?.hideProgress()
                view?.displayArtistInfo(artist.id, artist.fullName)
            },
            { error ->
                view?.hideProgress()
                view?.showError(error)
                if (error.contains("authenticated") || error.contains("401")) {
                    view?.navigateToLogin()
                }
            }
        )
    }

    override fun submitCommissionRequest(
        title: String,
        description: String,
        artistId: String
    ) {
        // Validate input
        if (title.isEmpty()) {
            view?.showError("Title is required")
            return
        }

        if (description.isEmpty()) {
            view?.showError("Description is required")
            return
        }

        if (artistId.isEmpty()) {
            view?.showError("Artist ID is invalid")
            return
        }

        if (!NetworkUtils.isNetworkAvailable(context)) {
            view?.showError("No internet connection")
            return
        }

        view?.showProgress()

        // Get current user email for the request
        userRepository.getProfile(object : UserRepository.ProfileCallback {
            override fun onSuccess(user: com.commicart.app.features.profile.data.models.User) {
                val request = SubmitCommissionRequest(
                    title = title,
                    description = description,
                    requesterEmail = user.email,
                    requesterName = user.fullName,
                    artistId = artistId
                )

                marketplaceRepository.submitCommissionRequest(
                    request,
                    { commissionRequest ->
                        view?.hideProgress()
                        view?.showRequestSubmitted(commissionRequest)
                        view?.showRequestSubmittedMessage("Commission request submitted successfully!")
                    },
                    { error ->
                        view?.hideProgress()
                        view?.showError(error)
                    }
                )
            }

            override fun onError(message: String) {
                view?.hideProgress()
                view?.showError("Failed to load user information: $message")
            }
        })
    }

    override fun onBackClick() {
        view?.navigateBack()
    }
}
