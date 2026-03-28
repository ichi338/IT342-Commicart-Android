// CustomerDashboardActivity.kt with ViewBinding
package com.commicart.app.ui.customer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.commicart.app.databinding.ActivityCustomerDashboardBinding
import com.commicart.app.data.repository.UserRepository
import com.commicart.app.ui.auth.LoginActivity
import com.commicart.app.ui.profile.ProfileActivity
import com.commicart.app.utils.TokenManager

class CustomerDashboardActivity : AppCompatActivity() {

    // ViewBinding instance
    private lateinit var binding: ActivityCustomerDashboardBinding
    private lateinit var userRepository: UserRepository
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityCustomerDashboardBinding.inflate(layoutInflater)
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

        // Browse Artists card
        binding.cardBrowseArtists.setOnClickListener {
            Toast.makeText(this, "Browse Artists - Coming Soon", Toast.LENGTH_SHORT).show()
        }

        // My Commissions card
        binding.cardMyCommissions.setOnClickListener {
            Toast.makeText(this, "My Commissions - Coming Soon", Toast.LENGTH_SHORT).show()
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
                    binding.tvWelcome.text = "Welcome, ${user.fullName}!"
                    binding.tvEmail.text = user.email
                    // Customer-specific UI updates
                }
            }

            override fun onError(message: String) {
                runOnUiThread {
                    binding.progressBar.visibility = android.view.View.GONE
                    Toast.makeText(this@CustomerDashboardActivity, message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}