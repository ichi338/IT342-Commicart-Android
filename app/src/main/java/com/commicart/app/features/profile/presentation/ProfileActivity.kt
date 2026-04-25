// features/profile/presentation/ProfileActivity.kt
package com.commicart.app.features.profile.presentation

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.commicart.app.R
import com.commicart.app.core.base.BaseActivity
import com.commicart.app.core.utils.*
import com.commicart.app.features.profile.domain.contracts.ProfileContract
import com.commicart.app.features.profile.data.models.User
import com.commicart.app.features.auth.data.repository.UserRepository
import com.commicart.app.features.profile.domain.presenters.ProfilePresenter
import com.commicart.app.features.artist.presentation.ArtistDashboardActivity
import com.commicart.app.features.auth.presentation.LoginActivity
import com.commicart.app.features.customer.presentation.CustomerDashboardActivity

class ProfileActivity : BaseActivity(), ProfileContract.View {

    private lateinit var presenter: ProfileContract.Presenter

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let { presenter.uploadProfileImage(it, this) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val userRepository = UserRepository(this)
        presenter = ProfilePresenter(this, userRepository)
        presenter.attachView(this)

        setupClickListeners()
        presenter.loadUserProfile()
    }

    private fun setupClickListeners() {
        onClick(R.id.btnBack) { presenter.onBackClick() }
        onClick(R.id.btnEditProfile) { presenter.onEditProfileClick() }
        onClick(R.id.btnChangePassword) { presenter.onChangePasswordClick() }
        onClick(R.id.ivProfileImage) { presenter.onImageClick() }
    }

    override fun showProgress() {
        showProgress(R.id.progressBar)
    }

    override fun hideProgress() {
        hideProgress(R.id.progressBar)
    }

    override fun displayUserInfo(user: User) {
        setText(R.id.tvFullName, user.fullName)
        setText(R.id.tvEmail, user.email)
        setText(R.id.tvBio, user.bio ?: "No bio added")

        if (user.phone != null && user.phone.isNotEmpty()) {
            setText(R.id.tvPhone, user.phone)
            show(R.id.tvPhone)
        } else {
            hide(R.id.tvPhone)
        }

        when (user.role.uppercase()) {
            "ARTIST" -> {
                setText(R.id.badgeRole, "Artist")
                show(R.id.badgeRole)
            }
            else -> {
                setText(R.id.badgeRole, "Customer")
                show(R.id.badgeRole)
            }
        }

        val memberSinceText = user.createdAt?.let {
            val date = if (it.length >= 10) it.substring(0, 10) else it
            "Member since: $date"
        } ?: "Member since: N/A"
        setText(R.id.tvMemberSince, memberSinceText)

        user.profilePictureUrl?.let { url ->
            val imageView = view<android.widget.ImageView>(R.id.ivProfileImage)
            Glide.with(this)
                .load(url)
                .circleCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(imageView)
        }
    }

    override fun showProfileUpdated(user: User) {
        displayUserInfo(user)
        toast("Profile updated!")
    }

    override fun showPasswordChanged() {
        toast("Password changed successfully!")
    }

    override fun showImageUploaded(user: User) {
        displayUserInfo(user)
        toast("Profile image updated!")
    }

    override fun showError(message: String) {
        toast(message)
    }

    override fun showEditProfileDialog(user: User) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val etFullName = dialogView.findViewById<EditText>(R.id.etFullName)
        val etBio = dialogView.findViewById<EditText>(R.id.etBio)
        val etPhone = dialogView.findViewById<EditText>(R.id.etPhone)

        etFullName.setText(user.fullName)
        etBio.setText(user.bio ?: "")
        etPhone.setText(user.phone ?: "")

        AlertDialog.Builder(this)
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val fullName = etFullName.text.toString().trim()
                val bio = etBio.text.toString().trim()
                val phone = etPhone.text.toString().trim()

                if (fullName.isNotEmpty()) {
                    presenter.updateProfile(fullName, bio, phone)
                } else {
                    toast("Full name is required")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val etCurrentPassword = dialogView.findViewById<EditText>(R.id.etCurrentPassword)
        val etNewPassword = dialogView.findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPassword = dialogView.findViewById<EditText>(R.id.etConfirmPassword)

        AlertDialog.Builder(this)
            .setTitle("Change Password")
            .setView(dialogView)
            .setPositiveButton("Change") { _, _ ->
                val currentPassword = etCurrentPassword.text.toString()
                val newPassword = etNewPassword.text.toString()
                val confirmPassword = etConfirmPassword.text.toString()

                if (currentPassword.isEmpty()) {
                    toast("Current password is required")
                    return@setPositiveButton
                }
                if (newPassword.isEmpty()) {
                    toast("New password is required")
                    return@setPositiveButton
                }
                if (newPassword.length < 6) {
                    toast("Password must be at least 6 characters")
                    return@setPositiveButton
                }
                if (newPassword != confirmPassword) {
                    toast("New passwords do not match")
                    return@setPositiveButton
                }

                presenter.changePassword(currentPassword, newPassword, confirmPassword)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    override fun navigateToDashboard(role: String) {
        val intent = when (role) {
            "ARTIST" -> Intent(this, ArtistDashboardActivity::class.java)
            else -> Intent(this, CustomerDashboardActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    override fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}