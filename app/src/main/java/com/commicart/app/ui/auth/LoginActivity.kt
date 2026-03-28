// ui/auth/LoginActivity.kt (Updated)
package com.commicart.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.commicart.app.databinding.ActivityLoginBinding
import com.commicart.app.data.models.LoginRequest
import com.commicart.app.data.repository.UserRepository
import com.commicart.app.ui.artist.ArtistDashboardActivity
import com.commicart.app.ui.customer.CustomerDashboardActivity
import com.commicart.app.utils.NetworkUtils
import com.commicart.app.utils.TokenManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userRepository: UserRepository
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = UserRepository(this)
        tokenManager = TokenManager(this)

        // Check if already logged in
        if (tokenManager.isLoggedIn()) {
            navigateBasedOnRole()
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener { performLogin() }
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            return
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            return
        }

        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Logging in..."
        binding.progressBar.visibility = android.view.View.VISIBLE

        val request = LoginRequest(email, password)

        userRepository.login(request, object : UserRepository.AuthCallback {
            override fun onSuccess(data: Any?) {
                runOnUiThread {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "Login"
                    binding.progressBar.visibility = android.view.View.GONE

                    val loginResponse = data as? com.commicart.app.data.models.LoginResponse
                    loginResponse?.let {
                        Toast.makeText(
                            this@LoginActivity,
                            "Welcome back, ${it.fullName}!",
                            Toast.LENGTH_SHORT
                        ).show()

                        navigateBasedOnRole()
                    }
                }
            }

            override fun onError(message: String) {
                runOnUiThread {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "Login"
                    binding.progressBar.visibility = android.view.View.GONE
                    Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun navigateBasedOnRole() {
        // Get user role from shared preferences or API
        userRepository.getProfile(object : UserRepository.ProfileCallback {
            override fun onSuccess(user: com.commicart.app.data.models.User) {
                when (user.role.uppercase()) {
                    "ARTIST" -> {
                        startActivity(Intent(this@LoginActivity, ArtistDashboardActivity::class.java))
                    }
                    else -> {
                        startActivity(Intent(this@LoginActivity, CustomerDashboardActivity::class.java))
                    }
                }
                finish()
            }

            override fun onError(message: String) {
                // Default to customer dashboard if role can't be determined
                startActivity(Intent(this@LoginActivity, CustomerDashboardActivity::class.java))
                finish()
            }
        })
    }
}