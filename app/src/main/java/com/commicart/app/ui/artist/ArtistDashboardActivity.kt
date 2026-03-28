// ArtistDashboardActivity.kt with ViewBinding
package com.commicart.app.ui.artist

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.commicart.app.databinding.ActivityArtistDashboardBinding
import com.commicart.app.data.repository.UserRepository
import com.commicart.app.ui.auth.LoginActivity
import com.commicart.app.ui.profile.ProfileActivity
import com.commicart.app.utils.TokenManager

class ArtistDashboardActivity : AppCompatActivity() {

    // ViewBinding instance
    private lateinit var binding: ActivityArtistDashboardBinding
    private lateinit var userRepository: UserRepository
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityArtistDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = UserRepository(this)
        tokenManager = TokenManager(this)

        setupClickListeners()
        loadUserProfile()
    }

    private fun setupClickListeners() {
        // Profile button
        binding.btnViewProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Manage Portfolio card (Artist-specific)
        binding.cardManagePortfolio.setOnClickListener {
            Toast.makeText(this, "Manage Portfolio - Coming Soon", Toast.LENGTH_SHORT).show()
        }

        // View Commissions card (Artist-specific)
        binding.cardViewCommissions.setOnClickListener {
            Toast.makeText(this, "View Commissions - Coming Soon", Toast.LENGTH_SHORT).show()
        }

        // Analytics card (Artist-specific)
        binding.cardAnalytics.setOnClickListener {
            Toast.makeText(this, "Analytics - Coming Soon", Toast.LENGTH_SHORT).show()
        }

        // Logout button
        binding.btnLogout.setOnClickListener {
            tokenManager.clearToken()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserProfile() {
        binding.progressBar.visibility = android.view.View.VISIBLE

        userRepository.getProfile(object : UserRepository.ProfileCallback {
            override fun onSuccess(user: com.commicart.app.data.models.User) {
                runOnUiThread {
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.tvWelcome.text = "Welcome back, Artist ${user.fullName}!"
                    binding.tvEmail.text = user.email

                    // Artist-specific stats
                    binding.tvEarnings.text = "₱0.00"
                    binding.tvActiveCommissions.text = "0"
                    binding.tvTotalSales.text = "0"
                    binding.tvPortfolioCount.text = "Items: 0"
                    binding.tvPendingRequests.text = "Pending: 0"
                    binding.tvThisMonthEarnings.text = "This month: ₱0"
                }
            }

            override fun onError(message: String) {
                runOnUiThread {
                    binding.progressBar.visibility = android.view.View.GONE
                    Toast.makeText(this@ArtistDashboardActivity, message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}