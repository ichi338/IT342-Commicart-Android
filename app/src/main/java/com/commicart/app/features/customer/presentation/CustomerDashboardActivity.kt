// features/customer/presentation/CustomerDashboardActivity.kt
package com.commicart.app.features.customer.presentation

import android.content.Intent
import android.os.Bundle
import com.commicart.app.R
import com.commicart.app.core.base.BaseActivity
import com.commicart.app.core.base.DashboardContract
import com.commicart.app.databinding.ActivityCustomerDashboardBinding
import com.commicart.app.features.profile.data.models.User
import com.commicart.app.features.auth.data.repository.UserRepository
import com.commicart.app.core.base.DashboardPresenter
import com.commicart.app.core.utils.*
import com.commicart.app.features.auth.presentation.LoginActivity
import com.commicart.app.features.profile.presentation.ProfileActivity

class CustomerDashboardActivity : BaseActivity(), DashboardContract.View {

    private lateinit var presenter: DashboardContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_dashboard)

        val userRepository = UserRepository(this)
        presenter = DashboardPresenter(this, userRepository, "CUSTOMER")
        presenter.attachView(this, "CUSTOMER")

        setupClickListeners()
        presenter.loadDashboardData()
    }

    private fun setupClickListeners() {
        onClick(R.id.btnViewProfile) { presenter.onViewProfileClick() }
        onClick(R.id.btnLogout) { presenter.onLogoutClick() }
        onClick(R.id.cardBrowseArtists) { presenter.onBrowseArtistsClick() }
        onClick(R.id.cardMyCommissions) { presenter.onMyCommissionsClick() }
    }

    override fun showProgress() {
        showProgress(R.id.progressBar)
    }

    override fun hideProgress() {
        hideProgress(R.id.progressBar)
    }

    override fun displayUserInfo(user: User) {
        setText(R.id.tvWelcome, "Welcome, ${user.fullName}!")
        setText(R.id.tvEmail, "Email: ${user.email}")
        setText(R.id.tvRoleBadge, user.role)
    }

    override fun showError(message: String) {
        toast(message)
    }

    override fun navigateToProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    override fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun showArtistSpecificData(portfolioCount: Int, pendingRequests: Int, earnings: Double, totalSales: Int) {
        // Not used for customer
    }

    override fun showCustomerSpecificData(activeCommissions: Int) {
        setText(R.id.tvActiveCommissions, "Active: $activeCommissions")
    }
}