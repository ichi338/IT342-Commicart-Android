package com.commicart.app.core.base

import android.content.Context
import com.commicart.app.features.auth.data.repository.UserRepository
import com.commicart.app.core.utils.TokenManager
import com.commicart.app.features.profile.data.models.User

class DashboardPresenter(
    private val context: Context,
    private val userRepository: UserRepository,
    private val role: String
) : DashboardContract.Presenter {

    private var view: DashboardContract.View? = null
    private val tokenManager = TokenManager(context)

    override fun attachView(view: DashboardContract.View, role: String) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadDashboardData() {
        view?.showProgress()

        userRepository.getProfile(object : UserRepository.ProfileCallback {
            override fun onSuccess(user: User) {
                view?.hideProgress()
                view?.displayUserInfo(user)

                when (user.role.uppercase()) {
                    "ARTIST" -> {
                        // Load artist-specific data
                        loadArtistData()
                    }
                    else -> {
                        // Load customer-specific data
                        loadCustomerData()
                    }
                }
            }

            override fun onError(message: String) {
                view?.hideProgress()
                view?.showError(message)

                if (message.contains("authenticated") || message.contains("401")) {
                    view?.navigateToLogin()
                }
            }
        })
    }

    private fun loadArtistData() {
        // Simulate loading artist data
        // In production, fetch from API
        val portfolioCount = 0
        val pendingRequests = 0
        val earnings = 0.0
        val totalSales = 0

        view?.showArtistSpecificData(portfolioCount, pendingRequests, earnings, totalSales)
    }

    private fun loadCustomerData() {
        // Simulate loading customer data
        // In production, fetch from API
        val activeCommissions = 0

        view?.showCustomerSpecificData(activeCommissions)
    }

    override fun onViewProfileClick() {
        view?.navigateToProfile()
    }

    override fun onLogoutClick() {
        tokenManager.clearToken()
        view?.navigateToLogin()
        view?.showError("Logged out successfully")
    }

    override fun onManagePortfolioClick() {
        // Navigate to portfolio management
    }

    override fun onViewCommissionsClick() {
        // Navigate to commissions view
    }

    override fun onAnalyticsClick() {
        // Navigate to analytics
    }

    override fun onBrowseArtistsClick() {
        // Navigate to browse artists
    }

    override fun onMyCommissionsClick() {
        // Navigate to my commissions
    }
}