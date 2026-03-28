// ui/auth/RegisterActivity.kt (Updated to receive role from RoleSelection)
package com.commicart.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.commicart.app.databinding.ActivityRegisterBinding
import com.commicart.app.data.models.RegisterRequest
import com.commicart.app.data.repository.UserRepository
import com.commicart.app.utils.NetworkUtils
import com.commicart.app.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userRepository: UserRepository
    private var selectedRole: String = "CUSTOMER" // Default

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get selected role from intent
        selectedRole = intent.getStringExtra("SELECTED_ROLE") ?: "CUSTOMER"

        userRepository = UserRepository(this)

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        // Show role badge at the top
        when (selectedRole) {
            "ARTIST" -> {
                binding.tvRoleBadge.text = "Registering as Artist"
                binding.tvRoleBadge.setBackgroundResource(R.drawable.badge_background_artist)
                binding.tvRoleHint.text = "Join as an Artist to sell your artwork!"
            }
            else -> {
                binding.tvRoleBadge.text = "Registering as Customer"
                binding.tvRoleBadge.setBackgroundResource(R.drawable.badge_background)
                binding.tvRoleHint.text = "Join as a Customer to commission amazing art!"
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener { performRegistration() }
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun performRegistration() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        // Validation
        if (fullName.isEmpty()) {
            binding.etFullName.error = "Full name is required"
            return
        }

        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Invalid email format"
            return
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            return
        }

        if (password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            return
        }

        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Passwords do not match"
            return
        }

        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnRegister.isEnabled = false
        binding.btnRegister.text = "Registering..."
        binding.progressBar.visibility = android.view.View.VISIBLE

        val request = RegisterRequest(email, password, fullName, selectedRole)

        userRepository.register(request, object : UserRepository.AuthCallback {
            override fun onSuccess(data: Any?) {
                runOnUiThread {
                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.text = "Register"
                    binding.progressBar.visibility = android.view.View.GONE

                    val message = if (selectedRole == "ARTIST") {
                        "Artist account created! Start selling your art."
                    } else {
                        "Customer account created! Find amazing artists."
                    }

                    Toast.makeText(
                        this@RegisterActivity,
                        "Registration successful!\n$message",
                        Toast.LENGTH_LONG
                    ).show()

                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                }
            }

            override fun onError(message: String) {
                runOnUiThread {
                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.text = "Register"
                    binding.progressBar.visibility = android.view.View.GONE
                    Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}