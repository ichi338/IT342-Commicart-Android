package com.commicart.app.presenters

import android.content.Context
import com.commicart.app.contracts.LoginContract
import com.commicart.app.data.models.LoginRequest
import com.commicart.app.data.repository.UserRepository
import com.commicart.app.utils.NetworkUtils
import com.commicart.app.utils.TokenManager
import java.util.regex.Pattern

class LoginPresenter(
    private val context: Context,
    private val userRepository: UserRepository
) : LoginContract.Presenter {

    private var view: LoginContract.View? = null
    private val tokenManager = TokenManager(context)

    override fun attachView(view: LoginContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun validateAndLogin(email: String, password: String) {
        // Validate inputs
        if (email.isEmpty()) {
            view?.setEmailError("Email is required")
            return
        }

        if (!isValidEmail(email)) {
            view?.setEmailError("Invalid email format")
            return
        }

        if (password.isEmpty()) {
            view?.setPasswordError("Password is required")
            return
        }

        if (password.length < 6) {
            view?.setPasswordError("Password must be at least 6 characters")
            return
        }

        if (!NetworkUtils.isNetworkAvailable(context)) {
            view?.showError("No internet connection")
            return
        }

        performLogin(email, password)
    }

    private fun performLogin(email: String, password: String) {
        view?.showProgress()

        val request = LoginRequest(email, password)

        userRepository.login(request, object : UserRepository.AuthCallback {
            override fun onSuccess(data: Any?) {
                val loginResponse = data as? com.commicart.app.data.models.LoginResponse
                loginResponse?.let {
                    onLoginSuccess(it)
                }
            }

            override fun onError(message: String) {
                onLoginError(message)
            }
        })
    }

    override fun onLoginSuccess(loginResponse: com.commicart.app.data.models.LoginResponse) {
        view?.hideProgress()
        view?.showLoginSuccess(loginResponse)

        // Determine role-based navigation
        val role = loginResponse.role.uppercase()
        view?.navigateToDashboard(role)
    }

    override fun onLoginError(message: String) {
        view?.hideProgress()
        view?.showError(message)
    }

    override fun onRegisterClick() {
        view?.navigateToRegister()
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)\$"
        )
        return emailPattern.matcher(email).matches()
    }
}