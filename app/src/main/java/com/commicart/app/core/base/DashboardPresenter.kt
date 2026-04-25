// core/base/DashboardPresenter.kt
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
                        loadArtistData()
                    }
                    else -> {
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
        val portfolioCount = 0
        val pendingRequests = 0
        val earnings = 0.0
        val totalSales = 0

        view?.showArtistSpecificData(portfolioCount, pendingRequests, earnings, totalSales)
    }

    private fun loadCustomerData() {
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
        if (context is android.app.Activity) {
            val intent = android.content.Intent(context, com.commicart.app.features.artist.presentation.PortfolioActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onViewCommissionsClick() {
        if (context is android.app.Activity) {
            val intent = android.content.Intent(context, com.commicart.app.features.artist.presentation.CommissionsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onAnalyticsClick() {
        // Not needed - removed
    }

    override fun onBrowseArtistsClick() {
        if (context is android.app.Activity) {
            val intent = android.content.Intent(context, com.commicart.app.features.customer.presentation.MarketplaceActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onMyCommissionsClick() {
        if (context is android.app.Activity) {
            android.widget.Toast.makeText(context, "My Commissions feature coming soon", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}