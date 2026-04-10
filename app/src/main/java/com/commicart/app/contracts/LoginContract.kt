package com.commicart.app.contracts
import com.commicart.app.data.models.LoginResponse
import com.commicart.app.data.models.User
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