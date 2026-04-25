package com.commicart.app.features.artist.domain.contracts

import com.commicart.app.features.artist.data.models.PortfolioItem

interface PortfolioContract {
    interface View {
        fun showProgress()
        fun hideProgress()
        fun displayPortfolioItems(items: List<PortfolioItem>)
        fun showPortfolioEmpty()
        fun showItemAdded(item: PortfolioItem)
        fun showItemUpdated(item: PortfolioItem)
        fun showItemDeleted(itemId: String)
        fun showError(message: String)
        fun navigateToAddItem()
        fun navigateToEditItem(itemId: String)
        fun navigateToLogin()
        fun navigateBack()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadPortfolioItems()
        fun onAddItemClick()
        fun onEditItemClick(itemId: String)
        fun onDeleteItemClick(itemId: String)
        fun addPortfolioItem(title: String, description: String, price: Double)
        fun updatePortfolioItem(itemId: String, title: String, description: String, price: Double)
        fun deletePortfolioItem(itemId: String)
        fun onBackClick()
    }
}
