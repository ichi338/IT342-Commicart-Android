package com.commicart.app.features.artist.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.commicart.app.core.base.DashboardContract
import com.commicart.app.databinding.ActivityArtistDashboardBinding
import com.commicart.app.features.profile.data.models.User
import com.commicart.app.features.auth.data.repository.UserRepository
import com.commicart.app.core.base.DashboardPresenter
import com.commicart.app.features.auth.presentation.LoginActivity
import com.commicart.app.features.profile.presentation.ProfileActivity
import java.text.NumberFormat
import java.util.Locale

class ArtistDashboardActivity : AppCompatActivity(), DashboardContract.View {

    private lateinit var binding: ActivityArtistDashboardBinding
    private lateinit var presenter: DashboardContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArtistDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userRepository = UserRepository(this)
        presenter = DashboardPresenter(this, userRepository, "ARTIST")
        presenter.attachView(this, "ARTIST")

        setupClickListeners()
        presenter.loadDashboardData()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    private fun setupClickListeners() {
        binding.btnViewProfile.setOnClickListener {
            presenter.onViewProfileClick()
        }

        binding.btnLogout.setOnClickListener {
            presenter.onLogoutClick()
        }

        binding.cardManagePortfolio.setOnClickListener {
            presenter.onManagePortfolioClick()
        }

        binding.cardViewCommissions.setOnClickListener {
            presenter.onViewCommissionsClick()
        }

        binding.cardAnalytics.setOnClickListener {
            presenter.onAnalyticsClick()
        }
    }

    override fun showProgress() {
        binding.progressBar.visibility = android.view.View.VISIBLE
    }

    override fun hideProgress() {
        binding.progressBar.visibility = android.view.View.GONE
    }

    override fun displayUserInfo(user: User) {
        binding.tvWelcome.text = "Welcome, ${user.fullName}!"
        binding.tvEmail.text = "Email: ${user.email}"
        binding.tvRoleBadge.text = user.role
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun navigateToProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    override fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun showArtistSpecificData(portfolioCount: Int, pendingRequests: Int, earnings: Double, totalSales: Int) {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "PH"))

        binding.tvPortfolioCount.text = "Items: $portfolioCount"
        binding.tvPendingRequests.text = "Pending: $pendingRequests"
        binding.tvEarnings.text = currencyFormat.format(earnings)
        binding.tvTotalSales.text = totalSales.toString()
        binding.tvActiveCommissions.text = pendingRequests.toString()
        binding.tvThisMonthEarnings.text = "This month: ${currencyFormat.format(earnings)}"
    }

    override fun showCustomerSpecificData(activeCommissions: Int) {
        // Not used for artist
    }
}