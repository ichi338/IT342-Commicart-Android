// features/artist/presentation/CommissionsActivity.kt
package com.commicart.app.features.artist.presentation

import android.content.Intent
import android.os.Bundle
import com.commicart.app.R
import com.commicart.app.core.base.BaseActivity
import com.commicart.app.core.utils.*
import com.commicart.app.features.artist.domain.contracts.CommissionContract
import com.commicart.app.features.artist.domain.presenters.CommissionPresenter
import com.commicart.app.features.artist.data.repository.CommissionRepository
import com.commicart.app.features.artist.data.models.Commission
import com.commicart.app.features.auth.presentation.LoginActivity

class CommissionsActivity : BaseActivity(), CommissionContract.View {

    private lateinit var presenter: CommissionContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commissions)

        val commissionRepository = CommissionRepository(this)
        presenter = CommissionPresenter(this, commissionRepository)
        presenter.attachView(this)

        setupUI()
        presenter.loadCommissions()
    }

    private fun setupUI() {
        onClick(R.id.btnBack) { presenter.onBackClick() }
        onClick(R.id.btnForApproval) {
            setActiveTab("FOR_APPROVAL")
            presenter.loadCommissionsByStatus("FOR_APPROVAL")
        }
        onClick(R.id.btnPending) {
            setActiveTab("PENDING")
            presenter.loadCommissionsByStatus("PENDING")
        }
        onClick(R.id.btnFinished) {
            setActiveTab("FINISHED")
            presenter.loadCommissionsByStatus("FINISHED")
        }

        setupRecyclerView(R.id.rvCommissions)
    }

    private fun setActiveTab(tabName: String) {
        val buttons = listOf(R.id.btnForApproval, R.id.btnPending, R.id.btnFinished)
        buttons.forEach { id ->
            val button = view<android.widget.Button>(id)
            button.isSelected = button.text.toString().uppercase().contains(tabName)
        }
    }

    override fun showProgress() {
        showProgress(R.id.progressBar)
    }

    override fun hideProgress() {
        hideProgress(R.id.progressBar)
    }

    override fun displayCommissions(commissions: List<Commission>) {
        hide(R.id.tvEmptyState)
        show(R.id.rvCommissions)
    }

    override fun displayCommissions(commissions: List<Commission>, status: String) {
        displayCommissions(commissions)
    }

    override fun showCommissionsEmpty() {
        show(R.id.tvEmptyState)
        setText(R.id.tvEmptyState, "No commissions found")
        hide(R.id.rvCommissions)
    }

    override fun showCommissionApproved(commission: Commission) {
        toast("Commission approved!")
        presenter.loadCommissions()
    }

    override fun showCommissionRejected(commissionId: String) {
        toast("Commission rejected!")
        presenter.loadCommissions()
    }

    override fun showCommissionCompleted(commission: Commission) {
        toast("Commission marked as completed!")
        presenter.loadCommissions()
    }

    override fun showCommissionUpdated(commission: Commission) {
        toast("Commission updated!")
        presenter.loadCommissions()
    }

    override fun showError(message: String) {
        toast(message)
    }

    override fun navigateToCommissionDetails(commissionId: String) {
        toast("Commission details feature coming soon")
    }

    override fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun navigateBack() {
        finish()
    }
}