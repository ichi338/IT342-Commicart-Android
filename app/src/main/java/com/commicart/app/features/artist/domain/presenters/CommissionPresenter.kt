package com.commicart.app.features.artist.domain.presenters

import android.content.Context
import com.commicart.app.features.artist.domain.contracts.CommissionContract
import com.commicart.app.features.artist.data.repository.CommissionRepository
import com.commicart.app.core.utils.NetworkUtils

class CommissionPresenter(
    private val context: Context,
    private val commissionRepository: CommissionRepository
) : CommissionContract.Presenter {

    private var view: CommissionContract.View? = null
    private var currentStatus: String? = null

    override fun attachView(view: CommissionContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadCommissions() {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            view?.showError("No internet connection")
            return
        }

        view?.showProgress()

        commissionRepository.getArtistCommissions(
            null,
            object : CommissionRepository.CommissionCallback {
                override fun onSuccess(commissions: List<com.commicart.app.features.artist.data.models.Commission>) {
                    view?.hideProgress()
                    if (commissions.isEmpty()) {
                        view?.showCommissionsEmpty()
                    } else {
                        view?.displayCommissions(commissions)
                    }
                }

                override fun onError(message: String) {
                    view?.hideProgress()
                    view?.showError(message)
                    if (message.contains("authenticated") || message.contains("401")) {
                        view?.navigateToLogin()
                    }
                }
            }
        )
    }

    override fun loadCommissionsByStatus(status: String) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            view?.showError("No internet connection")
            return
        }

        currentStatus = status
        view?.showProgress()

        commissionRepository.getArtistCommissions(
            status,
            object : CommissionRepository.CommissionCallback {
                override fun onSuccess(commissions: List<com.commicart.app.features.artist.data.models.Commission>) {
                    view?.hideProgress()
                    if (commissions.isEmpty()) {
                        view?.showCommissionsEmpty()
                    } else {
                        view?.displayCommissions(commissions, status)
                    }
                }

                override fun onError(message: String) {
                    view?.hideProgress()
                    view?.showError(message)
                }
            }
        )
    }

    override fun onCommissionClick(commissionId: String) {
        view?.navigateToCommissionDetails(commissionId)
    }

    override fun approveCommission(commissionId: String) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            view?.showError("No internet connection")
            return
        }

        view?.showProgress()

        commissionRepository.approveCommission(commissionId, object : CommissionRepository.SingleCommissionCallback {
            override fun onSuccess(commission: com.commicart.app.features.artist.data.models.Commission) {
                view?.hideProgress()
                view?.showCommissionApproved(commission)
                loadCommissions()
            }

            override fun onError(message: String) {
                view?.hideProgress()
                view?.showError(message)
            }
        })
    }

    override fun rejectCommission(commissionId: String, reason: String?) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            view?.showError("No internet connection")
            return
        }

        view?.showProgress()

        commissionRepository.rejectCommission(commissionId, reason, object : CommissionRepository.SingleCommissionCallback {
            override fun onSuccess(commission: com.commicart.app.features.artist.data.models.Commission) {
                view?.hideProgress()
                view?.showCommissionRejected(commissionId)
                loadCommissions()
            }

            override fun onError(message: String) {
                view?.hideProgress()
                view?.showError(message)
            }
        })
    }

    override fun completeCommission(commissionId: String) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            view?.showError("No internet connection")
            return
        }

        view?.showProgress()

        commissionRepository.completeCommission(commissionId, object : CommissionRepository.SingleCommissionCallback {
            override fun onSuccess(commission: com.commicart.app.features.artist.data.models.Commission) {
                view?.hideProgress()
                view?.showCommissionCompleted(commission)
                loadCommissions()
            }

            override fun onError(message: String) {
                view?.hideProgress()
                view?.showError(message)
            }
        })
    }

    override fun updateCommissionStatus(commissionId: String, newStatus: String) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            view?.showError("No internet connection")
            return
        }

        view?.showProgress()

        commissionRepository.updateCommissionStatus(commissionId, newStatus, object : CommissionRepository.SingleCommissionCallback {
            override fun onSuccess(commission: com.commicart.app.features.artist.data.models.Commission) {
                view?.hideProgress()
                view?.showCommissionUpdated(commission)
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
