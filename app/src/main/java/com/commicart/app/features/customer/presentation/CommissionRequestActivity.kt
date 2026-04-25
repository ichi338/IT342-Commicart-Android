// features/customer/presentation/CommissionRequestActivity.kt
package com.commicart.app.features.customer.presentation

import android.content.Intent
import android.os.Bundle
import com.commicart.app.R
import com.commicart.app.core.base.BaseActivity
import com.commicart.app.core.utils.*
import com.commicart.app.features.customer.domain.contracts.CommissionRequestContract
import com.commicart.app.features.customer.domain.presenters.CommissionRequestPresenter
import com.commicart.app.features.customer.data.repository.MarketplaceRepository
import com.commicart.app.features.customer.data.models.CommissionRequest
import com.commicart.app.features.auth.data.repository.UserRepository
import com.commicart.app.features.auth.presentation.LoginActivity

class CommissionRequestActivity : BaseActivity(), CommissionRequestContract.View {

    private lateinit var presenter: CommissionRequestContract.Presenter
    private var artistId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commission_request)

        artistId = intent.getStringExtra("ARTIST_ID")

        if (artistId.isNullOrEmpty()) {
            toast("Invalid artist")
            finish()
            return
        }

        val marketplaceRepository = MarketplaceRepository(this)
        val userRepository = UserRepository(this)
        presenter = CommissionRequestPresenter(this, marketplaceRepository, userRepository)
        presenter.attachView(this)

        setupUI()
        presenter.loadArtistInfo(artistId!!)
    }

    private fun setupUI() {
        onClick(R.id.btnBack) { presenter.onBackClick() }

        onClick(R.id.btnSubmitRequest) {
            val title = getEditTextValue(R.id.etTitle)
            val description = getEditTextValue(R.id.etDescription)

            if (title.isEmpty()) {
                toast("Please enter a commission title")
                return@onClick
            }

            if (description.isEmpty()) {
                toast("Please enter a commission description")
                return@onClick
            }

            presenter.submitCommissionRequest(title, description, artistId!!)
        }
    }

    override fun showProgress() {
        showProgress(R.id.progressBar)
        disable(R.id.btnSubmitRequest)
    }

    override fun hideProgress() {
        hideProgress(R.id.progressBar)
        enable(R.id.btnSubmitRequest)
    }

    override fun displayArtistInfo(artistId: String, artistName: String) {
        setText(R.id.tvArtistName, artistName)
    }

    override fun showRequestSubmitted(request: CommissionRequest) {
        toast("Commission request submitted!")
    }

    override fun showRequestSubmittedMessage(message: String) {
        toast(message)
        clearEditText(R.id.etTitle)
        clearEditText(R.id.etDescription)
        // Navigate back after a short delay
        android.os.Handler().postDelayed({
            finish()
        }, 1500)
    }

    override fun showError(message: String) {
        toast(message)
    }

    override fun navigateBack() {
        finish()
    }

    override fun navigateToMarketplace() {
        startActivity(Intent(this, MarketplaceActivity::class.java))
        finish()
    }

    override fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}