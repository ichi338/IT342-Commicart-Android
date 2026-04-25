// features/auth/presentation/LoginActivity.kt
package com.commicart.app.features.auth.presentation

import android.content.Intent
import android.os.Bundle
import com.commicart.app.R
import com.commicart.app.core.base.BaseActivity
import com.commicart.app.core.utils.*
import com.commicart.app.features.auth.domain.contracts.LoginContract
import com.commicart.app.features.auth.data.models.LoginResponse
import com.commicart.app.features.auth.data.repository.UserRepository
import com.commicart.app.features.auth.domain.presenters.LoginPresenter
import com.commicart.app.features.artist.presentation.ArtistDashboardActivity
import com.commicart.app.features.customer.presentation.CustomerDashboardActivity

class LoginActivity : BaseActivity(), LoginContract.View {

    private lateinit var presenter: LoginContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val userRepository = UserRepository(this)
        presenter = LoginPresenter(this, userRepository)
        presenter.attachView(this)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        onClick(R.id.btnLogin) {
            val email = getEditTextValue(R.id.etEmail)
            val password = getEditTextValue(R.id.etPassword)
            presenter.validateAndLogin(email, password)
        }

        onClick(R.id.tvLogin) {
            presenter.onRegisterClick()
        }
    }

    override fun showProgress() {
        disable(R.id.btnLogin)
        setButtonText(R.id.btnLogin, "Logging in...")
        showProgress(R.id.progressBar)
    }

    override fun hideProgress() {
        enable(R.id.btnLogin)
        setButtonText(R.id.btnLogin, "Login")
        hideProgress(R.id.progressBar)
    }

    override fun showLoginSuccess(loginResponse: LoginResponse) {
        toast("Welcome back, ${loginResponse.fullName}!")
    }

    override fun showError(message: String) {
        toast(message)
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
        setEditTextError(R.id.etEmail, error)
    }

    override fun setPasswordError(error: String) {
        setEditTextError(R.id.etPassword, error)
    }
}