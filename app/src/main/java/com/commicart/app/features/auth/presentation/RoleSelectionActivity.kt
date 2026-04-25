// features/auth/presentation/RoleSelectionActivity.kt
package com.commicart.app.features.auth.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.commicart.app.R
import com.commicart.app.core.utils.*

class RoleSelectionActivity : AppCompatActivity() {

    private var selectedRole: String = "CUSTOMER"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role_selection)

        setupRoleSelection()
        setupClickListeners()
        updateRoleSelection() // Initial update to set default selection
    }

    private fun setupRoleSelection() {
        onClick(R.id.cardArtist) {
            selectedRole = "ARTIST"
            updateRoleSelection()
        }

        onClick(R.id.cardCustomer) {
            selectedRole = "CUSTOMER"
            updateRoleSelection()
        }
    }

    private fun updateRoleSelection() {
        if (selectedRole == "ARTIST") {
            // Artist selected - use selected background
            findViewById<androidx.cardview.widget.CardView>(R.id.cardArtist).apply {
                setBackgroundResource(R.drawable.card_selected_bg)
            }
            show(R.id.ivArtistCheck)

            // Customer unselected - use unselected background
            findViewById<androidx.cardview.widget.CardView>(R.id.cardCustomer).apply {
                setBackgroundResource(R.drawable.card_unselected_bg)
            }
            hide(R.id.ivCustomerCheck)
        } else {
            // Customer selected - use selected background
            findViewById<androidx.cardview.widget.CardView>(R.id.cardCustomer).apply {
                setBackgroundResource(R.drawable.card_selected_bg)
            }
            show(R.id.ivCustomerCheck)

            // Artist unselected - use unselected background
            findViewById<androidx.cardview.widget.CardView>(R.id.cardArtist).apply {
                setBackgroundResource(R.drawable.card_unselected_bg)
            }
            hide(R.id.ivArtistCheck)
        }
    }

    private fun setupClickListeners() {
        onClick(R.id.btnCreateAccount) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra("SELECTED_ROLE", selectedRole)
            startActivity(intent)
        }

        onClick(R.id.tvLogin) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}