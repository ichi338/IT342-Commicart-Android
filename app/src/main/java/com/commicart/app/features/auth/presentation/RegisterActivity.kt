// features/auth/presentation/RegisterActivity.kt
package com.commicart.app.features.auth.presentation

import android.content.Intent
import android.os.Bundle
import com.commicart.app.R
import com.commicart.app.core.base.BaseActivity
import com.commicart.app.core.utils.*
import com.commicart.app.features.auth.domain.contracts.RegisterContract
import com.commicart.app.features.auth.data.repository.UserRepository
import com.commicart.app.features.auth.domain.presenters.RegisterPresenter

class RegisterActivity : BaseActivity(), RegisterContract.View {

    private lateinit var presenter: RegisterContract.Presenter
    private var selectedRole: String = "CUSTOMER"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        selectedRole = intent.getStringExtra("SELECTED_ROLE") ?: "CUSTOMER"

        val userRepository = UserRepository(this)
        presenter = RegisterPresenter(this, userRepository)
        presenter.attachView(this)

        updateRoleUI(selectedRole)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        onClick(R.id.btnRegister) {
            val fullName = getEditTextValue(R.id.etFullName)
            val email = getEditTextValue(R.id.etEmail)
            val password = getEditTextValue(R.id.etPassword)
            val confirmPassword = getEditTextValue(R.id.etConfirmPassword)

            presenter.validateAndRegister(fullName, email, password, confirmPassword, selectedRole)
        }

        onClick(R.id.tvLogin) {
            presenter.onLoginClick()
        }
    }

    override fun showProgress() {
        disable(R.id.btnRegister)
        setButtonText(R.id.btnRegister, "Registering...")
        showProgress(R.id.progressBar)
    }

    override fun hideProgress() {
        enable(R.id.btnRegister)
        setButtonText(R.id.btnRegister, "Register")
        hideProgress(R.id.progressBar)
    }

    override fun showRegistrationSuccess(message: String) {
        toast(message)
    }

    override fun showError(message: String) {
        toast(message)
    }

    override fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun setFullNameError(error: String) {
        setEditTextError(R.id.etFullName, error)
    }

    override fun setEmailError(error: String) {
        setEditTextError(R.id.etEmail, error)
    }

    override fun setPasswordError(error: String) {
        setEditTextError(R.id.etPassword, error)
    }

    override fun setConfirmPasswordError(error: String) {
        setEditTextError(R.id.etConfirmPassword, error)
    }

    override fun updateRoleUI(role: String) {
        when (role) {
            "ARTIST" -> {
                setText(R.id.tvRoleBadge, "Registering as Artist")
                setText(R.id.tvRoleHint, "Join as an Artist to sell your artwork!")
            }
            else -> {
                setText(R.id.tvRoleBadge, "Registering as Customer")
                setText(R.id.tvRoleHint, "Join as a Customer to commission amazing art!")
            }
        }
    }
}