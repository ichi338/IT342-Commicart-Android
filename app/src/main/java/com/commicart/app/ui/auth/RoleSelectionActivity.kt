package com.commicart.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.commicart.app.databinding.ActivityRoleSelectionBinding
import com.commicart.app.R

class RoleSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoleSelectionBinding
    private var selectedRole: String = "CUSTOMER" // Default to CUSTOMER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoleSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRoleSelection()
        setupClickListeners()
    }

    private fun setupRoleSelection() {
        // Artist card click
        binding.cardArtist.setOnClickListener {
            selectedRole = "ARTIST"
            updateRoleSelection()
        }

        // Customer card click
        binding.cardCustomer.setOnClickListener {
            selectedRole = "CUSTOMER"
            updateRoleSelection()
        }
    }

    private fun updateRoleSelection() {
        // Update Artist card style
        if (selectedRole == "ARTIST") {
            // Artist selected styling
            binding.cardArtist.setCardBackgroundColor(
                androidx.core.content.ContextCompat.getColor(this, R.color.purple_100)
            )
            binding.cardArtist.strokeWidth = 2
            binding.cardArtist.strokeColor = androidx.core.content.ContextCompat.getColor(this, R.color.purple_500)
            binding.ivArtistCheck.visibility = android.view.View.VISIBLE
            binding.tvArtistDescription.setTextColor(
                androidx.core.content.ContextCompat.getColor(this, R.color.purple_500)
            )

            // Customer unselected styling
            binding.cardCustomer.setCardBackgroundColor(
                androidx.core.content.ContextCompat.getColor(this, android.R.color.white)
            )
            binding.cardCustomer.strokeWidth = 0
            binding.ivCustomerCheck.visibility = android.view.View.GONE
            binding.tvCustomerDescription.setTextColor(
                androidx.core.content.ContextCompat.getColor(this, R.color.black)
            )
        } else {
            // Customer selected styling
            binding.cardCustomer.setCardBackgroundColor(
                androidx.core.content.ContextCompat.getColor(this, R.color.purple_100)
            )
            binding.cardCustomer.strokeWidth = 2
            binding.cardCustomer.strokeColor = androidx.core.content.ContextCompat.getColor(this, R.color.purple_500)
            binding.ivCustomerCheck.visibility = android.view.View.VISIBLE
            binding.tvCustomerDescription.setTextColor(
                androidx.core.content.ContextCompat.getColor(this, R.color.purple_500)
            )

            // Artist unselected styling
            binding.cardArtist.setCardBackgroundColor(
                androidx.core.content.ContextCompat.getColor(this, android.R.color.white)
            )
            binding.cardArtist.strokeWidth = 0
            binding.ivArtistCheck.visibility = android.view.View.GONE
            binding.tvArtistDescription.setTextColor(
                androidx.core.content.ContextCompat.getColor(this, R.color.black)
            )
        }
    }

    private fun setupClickListeners() {
        binding.btnCreateAccount.setOnClickListener {
            // Pass selected role to RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra("SELECTED_ROLE", selectedRole)
            startActivity(intent)
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}