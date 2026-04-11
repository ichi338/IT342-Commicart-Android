package com.commicart.app.features.auth.domain.contracts
import com.commicart.app.features.auth.data.models.LoginResponse

interface LoginContract {
    interface View {
        fun showProgress()
        fun hideProgress()
        fun showLoginSuccess(loginResponse: LoginResponse)
        fun showError(message: String)
        fun navigateToDashboard(role: String)
        fun navigateToRegister()
        fun setEmailError(error: String)
        fun setPasswordError(error: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun validateAndLogin(email: String, password: String)
        fun onLoginSuccess(loginResponse: LoginResponse)
        fun onLoginError(message: String)
        fun onRegisterClick()
    }
}