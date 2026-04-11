package com.commicart.app.features.auth.domain.contracts

interface RegisterContract {
    interface View {
        fun showProgress()
        fun hideProgress()
        fun showRegistrationSuccess(message: String)
        fun showError(message: String)
        fun navigateToLogin()
        fun setFullNameError(error: String)
        fun setEmailError(error: String)
        fun setPasswordError(error: String)
        fun setConfirmPasswordError(error: String)
        fun updateRoleUI(role: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun validateAndRegister(fullName: String, email: String, password: String, confirmPassword: String, role: String)
        fun onRegistrationSuccess()
        fun onRegistrationError(message: String)
        fun onLoginClick()
    }
}