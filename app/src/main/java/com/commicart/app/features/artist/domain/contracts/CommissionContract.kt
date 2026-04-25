package com.commicart.app.features.artist.domain.contracts

import com.commicart.app.features.artist.data.models.Commission

interface CommissionContract {
    interface View {
        fun showProgress()
        fun hideProgress()
        fun displayCommissions(commissions: List<Commission>)
        fun displayCommissions(commissions: List<Commission>, status: String)
        fun showCommissionsEmpty()
        fun showCommissionApproved(commission: Commission)
        fun showCommissionRejected(commissionId: String)
        fun showCommissionCompleted(commission: Commission)
        fun showCommissionUpdated(commission: Commission)
        fun showError(message: String)
        fun navigateToCommissionDetails(commissionId: String)
        fun navigateToLogin()
        fun navigateBack()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadCommissions()
        fun loadCommissionsByStatus(status: String)
        fun onCommissionClick(commissionId: String)
        fun approveCommission(commissionId: String)
        fun rejectCommission(commissionId: String, reason: String? = null)
        fun completeCommission(commissionId: String)
        fun updateCommissionStatus(commissionId: String, newStatus: String)
        fun onBackClick()
    }
}
