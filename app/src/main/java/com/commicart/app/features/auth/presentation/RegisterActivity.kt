package com.commicart.app.features.auth.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.commicart.app.R
import com.commicart.app.features.auth.domain.contracts.RegisterContract
import com.commicart.app.databinding.ActivityRegisterBinding
import com.commicart.app.features.auth.data.repository.UserRepository
import com.commicart.app.features.auth.domain.presenters.RegisterPresenter

class RegisterActivity : AppCompatActivity(), RegisterContract.View {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var presenter: RegisterContract.Presenter
    private var selectedRole: String = "CUSTOMER"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedRole = intent.getStringExtra("SELECTED_ROLE") ?: "CUSTOMER"

        val userRepository = UserRepository(this)
        presenter = RegisterPresenter(this, userRepository)
        presenter.attachView(this)

        setupUI()
        setupClickListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    private fun setupUI() {
        updateRoleUI(selectedRole)
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            presenter.validateAndRegister(fullName, email, password, confirmPassword, selectedRole)
        }

        binding.tvLogin.setOnClickListener {
            presenter.onLoginClick()
        }
    }

    override fun showProgress() {
        binding.btnRegister.isEnabled = false
        binding.btnRegister.text = "Registering..."
        binding.progressBar.visibility = android.view.View.VISIBLE
    }

    override fun hideProgress() {
        binding.btnRegister.isEnabled = true
        binding.btnRegister.text = "Register"
        binding.progressBar.visibility = android.view.View.GONE
    }

    override fun showRegistrationSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun setFullNameError(error: String) {
        binding.etFullName.error = error
    }

    override fun setEmailError(error: String) {
        binding.etEmail.error = error
    }

    override fun setPasswordError(error: String) {
        binding.etPassword.error = error
    }

    override fun setConfirmPasswordError(error: String) {
        binding.etConfirmPassword.error = error
    }

    override fun updateRoleUI(role: String) {
        when (role) {
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
}