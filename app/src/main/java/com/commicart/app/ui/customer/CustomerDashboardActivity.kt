package com.commicart.app.ui.customer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.commicart.app.R
import com.commicart.app.contracts.DashboardContract
import com.commicart.app.databinding.ActivityCustomerDashboardBinding
import com.commicart.app.data.models.User
import com.commicart.app.data.repository.UserRepository
import com.commicart.app.presenters.DashboardPresenter
import com.commicart.app.ui.auth.LoginActivity
import com.commicart.app.ui.profile.ProfileActivity

class CustomerDashboardActivity : AppCompatActivity(), DashboardContract.View {

    private lateinit var binding: ActivityCustomerDashboardBinding
    private lateinit var presenter: DashboardContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userRepository = UserRepository(this)
        presenter = DashboardPresenter(this, userRepository, "CUSTOMER")
        presenter.attachView(this, "CUSTOMER")

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

        binding.cardBrowseArtists.setOnClickListener {
            presenter.onBrowseArtistsClick()
        }

        binding.cardMyCommissions.setOnClickListener {
            presenter.onMyCommissionsClick()
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
        // Not used for customer
    }

    override fun showCustomerSpecificData(activeCommissions: Int) {
        binding.tvActiveCommissions.text = "Active: $activeCommissions"
    }
}