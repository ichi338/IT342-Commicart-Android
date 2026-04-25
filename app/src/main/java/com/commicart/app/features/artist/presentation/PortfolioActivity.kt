// features/artist/presentation/PortfolioActivity.kt
package com.commicart.app.features.artist.presentation

import android.content.Intent
import android.os.Bundle
import com.commicart.app.R
import com.commicart.app.core.base.BaseActivity
import com.commicart.app.core.utils.*
import com.commicart.app.features.artist.domain.contracts.PortfolioContract
import com.commicart.app.features.artist.domain.presenters.PortfolioPresenter
import com.commicart.app.features.artist.data.repository.PortfolioRepository
import com.commicart.app.features.artist.data.models.PortfolioItem
import com.commicart.app.features.auth.presentation.LoginActivity

class PortfolioActivity : BaseActivity(), PortfolioContract.View {

    private lateinit var presenter: PortfolioContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portfolio)

        val portfolioRepository = PortfolioRepository(this)
        presenter = PortfolioPresenter(this, portfolioRepository)
        presenter.attachView(this)

        setupUI()
        presenter.loadPortfolioItems()
    }

    private fun setupUI() {
        onClick(R.id.btnAddItem) { presenter.onAddItemClick() }
        setupRecyclerView(R.id.rvPortfolioItems)
    }

    override fun displayPortfolioItems(items: List<PortfolioItem>) {
        hide(R.id.tvEmptyState)
        show(R.id.rvPortfolioItems)
        // Update your adapter with items
    }

    override fun showProgress() {
        showProgress(R.id.progressBar)
    }

    override fun hideProgress() {
        hideProgress(R.id.progressBar)
    }


    override fun showPortfolioEmpty() {
        show(R.id.tvEmptyState)
        setText(R.id.tvEmptyState, "No portfolio items yet")
        hide(R.id.rvPortfolioItems)
    }

    override fun showItemAdded(item: PortfolioItem) {
        toast("Portfolio item added successfully!")
        presenter.loadPortfolioItems()
    }

    override fun showItemUpdated(item: PortfolioItem) {
        toast("Portfolio item updated!")
        presenter.loadPortfolioItems()
    }

    override fun showItemDeleted(itemId: String) {
        toast("Portfolio item deleted!")
    }

    override fun showError(message: String) {
        toast(message)
    }

    override fun navigateToAddItem() {
        toast("Add item feature coming soon")
    }

    override fun navigateToEditItem(itemId: String) {
        toast("Edit item feature coming soon")
    }

    override fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun navigateBack() {
        finish()
    }
}