// features/artist/domain/presenters/PortfolioPresenter.kt
package com.commicart.app.features.artist.domain.presenters

import android.content.Context
import com.commicart.app.features.artist.domain.contracts.PortfolioContract
import com.commicart.app.features.artist.data.repository.PortfolioRepository
import com.commicart.app.core.utils.NetworkUtils
import com.commicart.app.features.artist.data.models.PortfolioItem

class PortfolioPresenter(
    private val context: Context,
    private val portfolioRepository: PortfolioRepository
) : PortfolioContract.Presenter {

    private var view: PortfolioContract.View? = null

    override fun attachView(view: PortfolioContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadPortfolioItems() {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            view?.showError("No internet connection")
            return
        }

        view?.showProgress()

        portfolioRepository.getArtistPortfolio(object : PortfolioRepository.PortfolioCallback {
            override fun onSuccess(items: List<PortfolioItem>) {  // Changed from single item to List
                view?.hideProgress()
                if (items.isEmpty()) {
                    view?.showPortfolioEmpty()
                } else {
                    view?.displayPortfolioItems(items)
                }
            }

            override fun onError(message: String) {
                view?.hideProgress()
                view?.showError(message)
            }
        })
    }

    override fun onAddItemClick() {
        view?.navigateToAddItem()
    }

    override fun onEditItemClick(itemId: String) {
        view?.navigateToEditItem(itemId)
    }

    override fun onDeleteItemClick(itemId: String) {
        deletePortfolioItem(itemId)
    }

    override fun addPortfolioItem(title: String, description: String, price: Double) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            view?.showError("No internet connection")
            return
        }

        if (title.isEmpty()) {
            view?.showError("Title is required")
            return
        }

        if (description.isEmpty()) {
            view?.showError("Description is required")
            return
        }

        if (price < 0) {
            view?.showError("Price must be greater than 0")
            return
        }

        view?.showProgress()

        portfolioRepository.addPortfolioItem(
            title,
            description,
            price,
            null,
            object : PortfolioRepository.SingleItemCallback {
                override fun onSuccess(item: PortfolioItem) {
                    view?.hideProgress()
                    view?.showItemAdded(item)
                }

                override fun onError(message: String) {
                    view?.hideProgress()
                    view?.showError(message)
                }
            }
        )
    }

    override fun updatePortfolioItem(itemId: String, title: String, description: String, price: Double) {
        // Similar implementation
        view?.showError("Update feature coming soon")
    }

    override fun deletePortfolioItem(itemId: String) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            view?.showError("No internet connection")
            return
        }

        view?.showProgress()

        portfolioRepository.deletePortfolioItem(itemId, object : PortfolioRepository.PortfolioCallback {
            override fun onSuccess(items: List<PortfolioItem>) {
                view?.hideProgress()
                view?.showItemDeleted(itemId)
                loadPortfolioItems()
            }

            override fun onError(message: String) {
                view?.hideProgress()
                view?.showError(message)
            }
        })
    }

    override fun onBackClick() {
        view?.navigateBack()
    }
}