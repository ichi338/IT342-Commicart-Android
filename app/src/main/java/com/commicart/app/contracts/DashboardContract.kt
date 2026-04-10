package com.commicart.app.contracts

import com.commicart.app.data.models.User

interface DashboardContract {
    interface View {
        fun showProgress()
        fun hideProgress()
        fun displayUserInfo(user: User)
        fun showError(message: String)
        fun navigateToProfile()
        fun navigateToLogin()
        fun showArtistSpecificData(portfolioCount: Int, pendingRequests: Int, earnings: Double, totalSales: Int)
        fun showCustomerSpecificData(activeCommissions: Int)
    }

    interface Presenter {
        fun attachView(view: View, role: String)
        fun detachView()
        fun loadDashboardData()
        fun onViewProfileClick()
        fun onLogoutClick()
        fun onManagePortfolioClick()
        fun onViewCommissionsClick()
        fun onAnalyticsClick()
        fun onBrowseArtistsClick()
        fun onMyCommissionsClick()
    }
}