// features/customer/presentation/MarketplaceActivity.kt
package com.commicart.app.features.customer.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import com.commicart.app.R
import com.commicart.app.core.base.BaseActivity
import com.commicart.app.core.utils.*
import com.commicart.app.features.customer.domain.contracts.MarketplaceContract
import com.commicart.app.features.customer.domain.presenters.MarketplacePresenter
import com.commicart.app.features.customer.data.repository.MarketplaceRepository
import com.commicart.app.features.customer.data.models.MarketplaceCommission
import com.commicart.app.features.customer.data.models.ArtistListing
import com.commicart.app.features.auth.presentation.LoginActivity

class MarketplaceActivity : BaseActivity(), MarketplaceContract.View {

    private lateinit var presenter: MarketplaceContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marketplace)

        val marketplaceRepository = MarketplaceRepository(this)
        presenter = MarketplacePresenter(this, marketplaceRepository)
        presenter.attachView(this)

        setupUI()
        presenter.loadAvailableCommissions()
        presenter.loadTopArtists()
    }

    private fun setupUI() {
        onClick(R.id.btnBack) { onBackPressedDispatcher.onBackPressed() }
        onClick(R.id.btnFilter) { toast("Filter feature coming soon") }

        setupGridRecyclerView(R.id.rvCommissions, 2)

        val searchView = view<SearchView>(R.id.searchCommissions)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    presenter.searchCommissions(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    presenter.onSearchQueryChanged(newText)
                }
                return true
            }
        })
    }

    override fun showProgress() {
        showProgress(R.id.progressBar)
    }

    override fun hideProgress() {
        hideProgress(R.id.progressBar)
    }

    override fun displayCommissions(commissions: List<MarketplaceCommission>) {
        hide(R.id.tvEmptyState)
        show(R.id.rvCommissions)
    }

    override fun displayTopArtists(artists: List<ArtistListing>) {
        if (artists.isNotEmpty()) {
            show(R.id.topArtistsSection)
        }
    }

    override fun displaySearchResults(commissions: List<MarketplaceCommission>) {
        displayCommissions(commissions)
    }

    override fun showCommissionsEmpty() {
        show(R.id.tvEmptyState)
        setText(R.id.tvEmptyState, "No commissions available")
        hide(R.id.rvCommissions)
    }

    override fun showSearchEmpty() {
        show(R.id.tvEmptyState)
        setText(R.id.tvEmptyState, "No results found")
        hide(R.id.rvCommissions)
    }

    override fun showError(message: String) {
        toast(message)
    }

    override fun navigateToCommissionDetails(commissionId: String) {
        toast("Commission details feature coming soon")
    }

    override fun navigateToArtistProfile(artistId: String) {
        toast("Artist profile feature coming soon")
    }

    override fun navigateToCommissionRequest(artistId: String) {
        val intent = Intent(this, CommissionRequestActivity::class.java)
        intent.putExtra("ARTIST_ID", artistId)
        startActivity(intent)
    }

    override fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}