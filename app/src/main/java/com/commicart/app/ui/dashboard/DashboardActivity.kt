package com.commicart.app.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.commicart.app.databinding.ActivityDashboardBinding
import com.commicart.app.data.repository.UserRepository
import com.commicart.app.ui.auth.LoginActivity
import com.commicart.app.ui.profile.ProfileActivity
import com.commicart.app.utils.TokenManager

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var userRepository: UserRepository
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = UserRepository(this)
        tokenManager = TokenManager(this)

        setupClickListeners()
        loadUserProfile()
    }

    private fun setupClickListeners() {
        binding.btnViewProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

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
                    binding.tvEmail.text = "Email: ${user.email}"
                    binding.tvRole.text = "Role: ${user.role}"
                    binding.tvBio.text = "Bio: ${user.bio ?: "No bio added yet"}"
                }
            }

            override fun onError(message: String) {
                runOnUiThread {
                    binding.progressBar.visibility = android.view.View.GONE
                    Toast.makeText(this@DashboardActivity, message, Toast.LENGTH_LONG).show()

                    if (message.contains("authenticated")) {
                        tokenManager.clearToken()
                        startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                        finish()
                    }
                }
            }
        })
    }
}