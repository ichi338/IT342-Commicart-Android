package com.commicart.app.presenters

import android.content.Context
import com.commicart.app.contracts.RegisterContract
import com.commicart.app.data.models.RegisterRequest
import com.commicart.app.data.repository.UserRepository
import com.commicart.app.utils.NetworkUtils
import java.util.regex.Pattern

class RegisterPresenter(
    private val context: Context,
    private val userRepository: UserRepository
) : RegisterContract.Presenter {

    private var view: RegisterContract.View? = null

    override fun attachView(view: RegisterContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun validateAndRegister(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        role: String
    ) {
        // Validate full name
        if (fullName.isEmpty()) {
            view?.setFullNameError("Full name is required")
            return
        }

        // Validate email
        if (email.isEmpty()) {
            view?.setEmailError("Email is required")
            return
        }

        if (!isValidEmail(email)) {
            view?.setEmailError("Invalid email format")
            return
        }

        // Validate password
        if (password.isEmpty()) {
            view?.setPasswordError("Password is required")
            return
        }

        if (password.length < 6) {
            view?.setPasswordError("Password must be at least 6 characters")
            return
        }

        // Validate password confirmation
        if (password != confirmPassword) {
            view?.setConfirmPasswordError("Passwords do not match")
            return
        }

        if (!NetworkUtils.isNetworkAvailable(context)) {
            view?.showError("No internet connection")
            return
        }

        performRegistration(fullName, email, password, role)
    }

    private fun performRegistration(fullName: String, email: String, password: String, role: String) {
        view?.showProgress()

        val request = RegisterRequest(email, password, fullName, role)

        userRepository.register(request, object : UserRepository.AuthCallback {
            override fun onSuccess(data: Any?) {
                onRegistrationSuccess()
            }

            override fun onError(message: String) {
                onRegistrationError(message)
            }
        })
    }

    override fun onRegistrationSuccess() {
        view?.hideProgress()
        val message = "Registration successful! Please login to continue."
        view?.showRegistrationSuccess(message)
        view?.navigateToLogin()
    }

    override fun onRegistrationError(message: String) {
        view?.hideProgress()
        view?.showError(message)
    }

    override fun onLoginClick() {
        view?.navigateToLogin()
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)\$"
        )
        return emailPattern.matcher(email).matches()
    }
}