package com.commicart.app.features.auth.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.commicart.app.features.auth.domain.contracts.LoginContract
import com.commicart.app.databinding.ActivityLoginBinding
import com.commicart.app.features.auth.data.models.LoginResponse
import com.commicart.app.features.auth.data.repository.UserRepository
import com.commicart.app.features.auth.domain.presenters.LoginPresenter
import com.commicart.app.features.artist.presentation.ArtistDashboardActivity
import com.commicart.app.features.customer.presentation.CustomerDashboardActivity

class LoginActivity : AppCompatActivity(), LoginContract.View {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var presenter: LoginContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userRepository = UserRepository(this)
        presenter = LoginPresenter(this, userRepository)
        presenter.attachView(this)

        setupClickListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            presenter.validateAndLogin(email, password)
        }

        binding.tvRegister.setOnClickListener {
            presenter.onRegisterClick()
        }
    }

    override fun showProgress() {
        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Logging in..."
        binding.progressBar.visibility = android.view.View.VISIBLE
    }

    override fun hideProgress() {
        binding.btnLogin.isEnabled = true
        binding.btnLogin.text = "Login"
        binding.progressBar.visibility = android.view.View.GONE
    }

    override fun showLoginSuccess(loginResponse: LoginResponse) {
        Toast.makeText(this, "Welcome back, ${loginResponse.fullName}!", Toast.LENGTH_SHORT).show()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun navigateToDashboard(role: String) {
        val intent = when (role) {
            "ARTIST" -> Intent(this, ArtistDashboardActivity::class.java)
            else -> Intent(this, CustomerDashboardActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    override fun navigateToRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    override fun setEmailError(error: String) {
        binding.etEmail.error = error
    }

    override fun setPasswordError(error: String) {
        binding.etPassword.error = error
    }
}