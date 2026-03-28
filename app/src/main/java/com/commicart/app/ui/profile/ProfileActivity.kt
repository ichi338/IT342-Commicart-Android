// ui/profile/ProfileActivity.kt
package com.commicart.app.ui.profile

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
import com.commicart.app.databinding.ActivityProfileBinding
import com.commicart.app.data.models.ChangePasswordRequest
import com.commicart.app.data.models.EditProfileRequest
import com.commicart.app.data.models.User
import com.commicart.app.data.repository.UserRepository
import com.commicart.app.ui.artist.ArtistDashboardActivity
import com.commicart.app.ui.auth.LoginActivity
import com.commicart.app.ui.customer.CustomerDashboardActivity
import com.commicart.app.utils.NetworkUtils
import com.commicart.app.utils.TokenManager
import java.io.File
import java.io.FileOutputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var userRepository: UserRepository
    private lateinit var tokenManager: TokenManager
    private var currentUser: User? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let { uploadProfileImage(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = UserRepository(this)
        tokenManager = TokenManager(this)

        setupClickListeners()
        loadUserProfile()
    }

    private fun setupClickListeners() {
        // Back button
        binding.btnBack.setOnClickListener {
            navigateToDashboard()
        }

        // Edit Profile button
        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        // Change Password button
        binding.btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        // Profile image click
        binding.ivProfileImage.setOnClickListener {
            openImagePicker()
        }
    }

    private fun loadUserProfile() {
        binding.progressBar.visibility = android.view.View.VISIBLE

        userRepository.getProfile(object : UserRepository.ProfileCallback {
            override fun onSuccess(user: User) {
                currentUser = user
                runOnUiThread {
                    binding.progressBar.visibility = android.view.View.GONE
                    displayUserInfo(user)
                }
            }

            override fun onError(message: String) {
                runOnUiThread {
                    binding.progressBar.visibility = android.view.View.GONE
                    Toast.makeText(this@ProfileActivity, message, Toast.LENGTH_LONG).show()

                    // If authentication error, redirect to login
                    if (message.contains("authenticated") || message.contains("401")) {
                        tokenManager.clearToken()
                        startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                        finish()
                    }
                }
            }
        })
    }

    private fun displayUserInfo(user: User) {
        // Basic info
        binding.tvFullName.text = user.fullName
        binding.tvEmail.text = user.email
        binding.tvBio.text = user.bio ?: "No bio added"

        // Handle phone field (if exists in backend)
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
            val date = it.substring(0, 10) // Extract YYYY-MM-DD
            "Member since: $date"
        } ?: "Member since: N/A"
        binding.tvMemberSince.text = memberSinceText

        // Load profile image if available
        user.profilePictureUrl?.let { url ->
            Glide.with(this)
                .load(url)
                .circleCrop()
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.ic_default_avatar)
                .into(binding.ivProfileImage)
        }
    }

    private fun showEditProfileDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val etFullName = dialogView.findViewById<android.widget.EditText>(R.id.etFullName)
        val etBio = dialogView.findViewById<android.widget.EditText>(R.id.etBio)
        val etPhone = dialogView.findViewById<android.widget.EditText>(R.id.etPhone)

        etFullName.setText(currentUser?.fullName ?: "")
        etBio.setText(currentUser?.bio ?: "")
        etPhone.setText(currentUser?.phone ?: "")

        AlertDialog.Builder(this)
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val fullName = etFullName.text.toString().trim()
                val bio = etBio.text.toString().trim()
                val phone = etPhone.text.toString().trim()

                if (fullName.isNotEmpty()) {
                    updateProfile(fullName, bio, phone)
                } else {
                    Toast.makeText(this, "Full name is required", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateProfile(fullName: String, bio: String, phone: String) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = android.view.View.VISIBLE

        val request = EditProfileRequest(
            fullName = fullName,
            bio = bio.ifEmpty { null },
            phone = phone.ifEmpty { null },
            email = null  // Email changes handled separately if needed
        )

        userRepository.updateProfile(request, object : UserRepository.ProfileCallback {
            override fun onSuccess(user: User) {
                currentUser = user
                runOnUiThread {
                    binding.progressBar.visibility = android.view.View.GONE
                    displayUserInfo(user)
                    Toast.makeText(this@ProfileActivity, "Profile updated!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onError(message: String) {
                runOnUiThread {
                    binding.progressBar.visibility = android.view.View.GONE
                    Toast.makeText(this@ProfileActivity, message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun showChangePasswordDialog() {
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

                when {
                    oldPassword.isEmpty() -> {
                        Toast.makeText(this, "Old password is required", Toast.LENGTH_SHORT).show()
                    }
                    newPassword.isEmpty() -> {
                        Toast.makeText(this, "New password is required", Toast.LENGTH_SHORT).show()
                    }
                    newPassword.length < 6 -> {
                        Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    }
                    newPassword != confirmPassword -> {
                        Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        changePassword(oldPassword, newPassword)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun changePassword(oldPassword: String, newPassword: String) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = android.view.View.VISIBLE

        val request = ChangePasswordRequest(oldPassword, newPassword)

        userRepository.changePassword(request, object : UserRepository.AuthCallback {
            override fun onSuccess(data: Any?) {
                runOnUiThread {
                    binding.progressBar.visibility = android.view.View.GONE
                    Toast.makeText(this@ProfileActivity, "Password changed successfully!", Toast.LENGTH_LONG).show()
                }
            }

            override fun onError(message: String) {
                runOnUiThread {
                    binding.progressBar.visibility = android.view.View.GONE
                    Toast.makeText(this@ProfileActivity, message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun uploadProfileImage(imageUri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(imageUri)
            val file = File(cacheDir, "profile_image_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }

            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
                return
            }

            binding.progressBar.visibility = android.view.View.VISIBLE

            userRepository.uploadProfileImage(file, object : UserRepository.ImageUploadCallback {
                override fun onSuccess(user: User) {
                    currentUser = user
                    runOnUiThread {
                        binding.progressBar.visibility = android.view.View.GONE
                        displayUserInfo(user)
                        Toast.makeText(this@ProfileActivity, "Profile image updated!", Toast.LENGTH_SHORT).show()
                        file.delete()
                    }
                }

                override fun onError(message: String) {
                    runOnUiThread {
                        binding.progressBar.visibility = android.view.View.GONE
                        Toast.makeText(this@ProfileActivity, message, Toast.LENGTH_LONG).show()
                        file.delete()
                    }
                }
            })
        } catch (e: Exception) {
            Toast.makeText(this, "Error processing image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToDashboard() {
        // Get user role and navigate to appropriate dashboard
        userRepository.getProfile(object : UserRepository.ProfileCallback {
            override fun onSuccess(user: User) {
                runOnUiThread {
                    when (user.role.uppercase()) {
                        "ARTIST" -> {
                            startActivity(Intent(this@ProfileActivity, ArtistDashboardActivity::class.java))
                        }
                        else -> {
                            startActivity(Intent(this@ProfileActivity, CustomerDashboardActivity::class.java))
                        }
                    }
                    finish()
                }
            }

            override fun onError(message: String) {
                runOnUiThread {
                    // Default to customer dashboard if error
                    startActivity(Intent(this@ProfileActivity, CustomerDashboardActivity::class.java))
                    finish()
                }
            }
        })
    }
}