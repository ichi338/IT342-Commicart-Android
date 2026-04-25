// features/artist/presentation/ArtistDashboardActivity.kt
package com.commicart.app.features.artist.presentation

import android.content.Intent
import android.os.Bundle
import com.commicart.app.R
import com.commicart.app.core.base.BaseActivity
import com.commicart.app.core.base.DashboardContract
import com.commicart.app.core.base.DashboardPresenter
import com.commicart.app.core.utils.*
import com.commicart.app.features.auth.data.repository.UserRepository
import com.commicart.app.features.auth.presentation.LoginActivity
import com.commicart.app.features.profile.data.models.User
import com.commicart.app.features.profile.presentation.ProfileActivity

class ArtistDashboardActivity : BaseActivity(), DashboardContract.View {

    private lateinit var presenter: DashboardContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_dashboard)

        val userRepository = UserRepository(this)
        presenter = DashboardPresenter(this, userRepository, "ARTIST")
        presenter.attachView(this, "ARTIST")

        setupClickListeners()
        presenter.loadDashboardData()
    }

    private fun setupClickListeners() {
        onClick(R.id.btnFollow) {
            toast("Follow feature coming soon")
        }
        onClick(R.id.btnManagePortfolio) { presenter.onManagePortfolioClick() }
        onClick(R.id.btnViewCommissions) { presenter.onViewCommissionsClick() }
    }

    override fun showProgress() {
        showProgress(R.id.progressBar)
    }

    override fun hideProgress() {
        hideProgress(R.id.progressBar)
    }

    override fun displayUserInfo(user: User) {
        setText(R.id.tvWelcome, "Now available for commission services!")
        setText(R.id.tvArtistName, user.fullName)
        setText(R.id.tvBio, user.bio ?: "Insert bio here...")
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
        // Update stats in the layout
        setText(R.id.tvPortfolioCount, "Portfolio\n$portfolioCount")
        setText(R.id.tvCommissionsCount, "Commissions\n$pendingRequests")
        setText(R.id.tvOnQueue, "On Queue\n0")
    }

    override fun showCustomerSpecificData(activeCommissions: Int) {
        // Not used for artist
    }
}