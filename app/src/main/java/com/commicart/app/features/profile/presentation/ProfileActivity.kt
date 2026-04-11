package com.commicart.app.features.profile.presentation

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.commicart.app.R
import com.commicart.app.features.profile.domain.contracts.ProfileContract
import com.commicart.app.databinding.ActivityProfileBinding
import com.commicart.app.features.profile.data.models.User
import com.commicart.app.features.auth.data.repository.UserRepository
import com.commicart.app.features.profile.domain.presenters.ProfilePresenter
import com.commicart.app.features.artist.presentation.ArtistDashboardActivity
import com.commicart.app.features.auth.presentation.LoginActivity
import com.commicart.app.features.customer.presentation.CustomerDashboardActivity

class ProfileActivity : AppCompatActivity(), ProfileContract.View {

    private lateinit var binding: ActivityProfileBinding
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
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userRepository = UserRepository(this)
        presenter = ProfilePresenter(this, userRepository)
        presenter.attachView(this)

        setupClickListeners()
        presenter.loadUserProfile()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            presenter.onBackClick()
        }

        binding.btnEditProfile.setOnClickListener {
            presenter.onEditProfileClick()
        }

        binding.btnChangePassword.setOnClickListener {
            presenter.onChangePasswordClick()
        }

        binding.ivProfileImage.setOnClickListener {
            presenter.onImageClick()
        }
    }

    override fun showProgress() {
        binding.progressBar.visibility = android.view.View.VISIBLE
    }

    override fun hideProgress() {
        binding.progressBar.visibility = android.view.View.GONE
    }

    override fun displayUserInfo(user: User) {
        binding.tvFullName.text = user.fullName
        binding.tvEmail.text = user.email
        binding.tvBio.text = user.bio ?: "No bio added"

        // Handle phone field
        user.phone?.let {
            binding.tvPhone.text = it
            binding.tvPhone.visibility = android.view.View.VISIBLE
        } ?: run {
            binding.tvPhone.visibility = android.view.View.GONE
        }

        // Handle role badge
        when (user.role.uppercase()) {
            "ARTIST" -> {
                binding.badgeRole.text = "Artist"
                binding.badgeRole.setBackgroundResource(R.drawable.badge_background_artist)
                binding.badgeRole.visibility = android.view.View.VISIBLE
            }
            else -> {
                binding.badgeRole.text = "Customer"
                binding.badgeRole.setBackgroundResource(R.drawable.badge_background)
                binding.badgeRole.visibility = android.view.View.VISIBLE
            }
        }

        // Member since
        val memberSinceText = user.createdAt?.let {
            val date = it.substring(0, 10)
            "Member since: $date"
        } ?: "Member since: N/A"
        binding.tvMemberSince.text = memberSinceText

        // Load profile image
        user.profilePictureUrl?.let { url ->
            Glide.with(this)
                .load(url)
                .circleCrop()
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.ic_default_avatar)
                .into(binding.ivProfileImage)
        }
    }

    override fun showProfileUpdated(user: User) {
        displayUserInfo(user)
        Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
    }

    override fun showPasswordChanged() {
        Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_LONG).show()
    }

    override fun showImageUploaded(user: User) {
        displayUserInfo(user)
        Toast.makeText(this, "Profile image updated!", Toast.LENGTH_SHORT).show()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showEditProfileDialog(user: User) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val etFullName = dialogView.findViewById<android.widget.EditText>(R.id.etFullName)
        val etBio = dialogView.findViewById<android.widget.EditText>(R.id.etBio)
        val etPhone = dialogView.findViewById<android.widget.EditText>(R.id.etPhone)

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
                    Toast.makeText(this, "Full name is required", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val etOldPassword = dialogView.findViewById<android.widget.EditText>(R.id.etOldPassword)
        val etNewPassword = dialogView.findViewById<android.widget.EditText>(R.id.etNewPassword)
        val etConfirmPassword = dialogView.findViewById<android.widget.EditText>(R.id.etConfirmPassword)

        AlertDialog.Builder(this)
            .setTitle("Change Password")
            .setView(dialogView)
            .setPositiveButton("Change") { _, _ ->
                val oldPassword = etOldPassword.text.toString()
                val newPassword = etNewPassword.text.toString()
                val confirmPassword = etConfirmPassword.text.toString()

                presenter.changePassword(oldPassword, newPassword, confirmPassword)
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