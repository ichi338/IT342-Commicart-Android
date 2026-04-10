package com.commicart.app.presenters

import android.content.Context
import android.net.Uri
import com.commicart.app.contracts.ProfileContract
import com.commicart.app.data.models.ChangePasswordRequest
import com.commicart.app.data.models.EditProfileRequest
import com.commicart.app.data.models.User
import com.commicart.app.data.repository.UserRepository
import com.commicart.app.utils.NetworkUtils
import java.io.File
import java.io.FileOutputStream

class ProfilePresenter(
    private val context: Context,
    private val userRepository: UserRepository
) : ProfileContract.Presenter {

    private var view: ProfileContract.View? = null
    private var currentUser: User? = null

    override fun attachView(view: ProfileContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadUserProfile() {
        view?.showProgress()

        userRepository.getProfile(object : UserRepository.ProfileCallback {
            override fun onSuccess(user: User) {
                currentUser = user
                view?.hideProgress()
                view?.displayUserInfo(user)
            }

            override fun onError(message: String) {
                view?.hideProgress()
                view?.showError(message)

                if (message.contains("authenticated") || message.contains("401")) {
                    view?.navigateToLogin()
                }
            }
        })
    }

    override fun updateProfile(fullName: String, bio: String, phone: String) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            view?.showError("No internet connection")
            return
        }

        view?.showProgress()

        val request = EditProfileRequest(
            fullName = fullName,
            bio = bio.ifEmpty { null },
            phone = phone.ifEmpty { null },
            email = null
        )

        userRepository.updateProfile(request, object : UserRepository.ProfileCallback {
            override fun onSuccess(user: User) {
                currentUser = user
                view?.hideProgress()
                view?.showProfileUpdated(user)
            }

            override fun onError(message: String) {
                view?.hideProgress()
                view?.showError(message)
            }
        })
    }

    override fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String) {
        if (oldPassword.isEmpty()) {
            view?.showError("Old password is required")
            return
        }

        if (newPassword.isEmpty()) {
            view?.showError("New password is required")
            return
        }

        if (newPassword.length < 6) {
            view?.showError("Password must be at least 6 characters")
            return
        }

        if (newPassword != confirmPassword) {
            view?.showError("New passwords do not match")
            return
        }

        if (!NetworkUtils.isNetworkAvailable(context)) {
            view?.showError("No internet connection")
            return
        }

        view?.showProgress()

        val request = ChangePasswordRequest(oldPassword, newPassword)

        userRepository.changePassword(request, object : UserRepository.AuthCallback {
            override fun onSuccess(data: Any?) {
                view?.hideProgress()
                view?.showPasswordChanged()
            }

            override fun onError(message: String) {
                view?.hideProgress()
                view?.showError(message)
            }
        })
    }

    override fun uploadProfileImage(imageUri: Uri, context: Context) {
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val file = File(context.cacheDir, "profile_image_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }

            if (!NetworkUtils.isNetworkAvailable(context)) {
                view?.showError("No internet connection")
                file.delete()
                return
            }

            view?.showProgress()

            userRepository.uploadProfileImage(file, object : UserRepository.ImageUploadCallback {
                override fun onSuccess(user: User) {
                    currentUser = user
                    view?.hideProgress()
                    view?.showImageUploaded(user)
                    file.delete()
                }

                override fun onError(message: String) {
                    view?.hideProgress()
                    view?.showError(message)
                    file.delete()
                }
            })
        } catch (e: Exception) {
            view?.showError("Error processing image: ${e.message}")
        }
    }

    override fun onEditProfileClick() {
        currentUser?.let { view?.showEditProfileDialog(it) }
    }

    override fun onChangePasswordClick() {
        view?.showChangePasswordDialog()
    }

    override fun onImageClick() {
        view?.openImagePicker()
    }

    override fun onBackClick() {
        currentUser?.let {
            view?.navigateToDashboard(it.role.uppercase())
        }
    }
}