package com.commicart.app.contracts
import com.commicart.app.data.models.User
interface ProfileContract {
    interface View {
        fun showProgress()
        fun hideProgress()
        fun displayUserInfo(user: User)
        fun showProfileUpdated(user: User)
        fun showPasswordChanged()
        fun showImageUploaded(user: User)
        fun showError(message: String)
        fun showEditProfileDialog(user: User)
        fun showChangePasswordDialog()
        fun openImagePicker()
        fun navigateToDashboard(role: String)
        fun navigateToLogin()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadUserProfile()
        fun updateProfile(fullName: String, bio: String, phone: String)
        fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String)
        fun uploadProfileImage(imageUri: android.net.Uri, context: android.content.Context)
        fun onEditProfileClick()
        fun onChangePasswordClick()
        fun onImageClick()
        fun onBackClick()
    }
}