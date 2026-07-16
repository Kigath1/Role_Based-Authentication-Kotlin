package com.example.Roomdb.viewmodel.employer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomdb.data.model.Job
import com.example.Roomdb.data.model.JobStatus
import com.example.Roomdb.data.model.PaymentStatus
import com.example.Roomdb.domain.usecases.employer.AcceptCounterOfferUseCase
import com.example.Roomdb.domain.usecases.employer.CancelJobUseCase
import com.example.Roomdb.domain.usecases.employer.CheckClientPaymentStatusUseCase
import com.example.Roomdb.domain.usecases.employer.FundEscrowUseCase
import com.example.Roomdb.domain.usecases.employer.GetClientJobsUseCase
import com.example.Roomdb.domain.usecases.employer.ReleaseEscrowUseCase
import com.example.Roomdb.domain.usecases.employer.SubmitReviewUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class JobFilter { ACTIVE, COMPLETED }

data class ClientJobsUiState(
    val isLoading: Boolean = false,
    val jobs: List<Job> = emptyList(),
    val filter: JobFilter = JobFilter.ACTIVE,
    val error: String? = null,
    val actionInProgressJobId: String? = null, // disables buttons on just that card during a call

    // ── Escrow / payment state ───────────────────────────
    val paymentStatusMap: Map<String, PaymentStatus> = emptyMap(),
    val fundingJobId: String? = null,               // shows the phone-number sheet
    val awaitingStkConfirmationJobId: String? = null, // shows the "check your phone" dialog
    val reviewPromptJobId: String? = null,           // shows the review dialog
    val reviewedJobIds: Set<String> = emptySet()     // tracked locally, see note below
) {
    val filteredJobs: List<Job>
        get() = jobs.filter { if (filter == JobFilter.ACTIVE) it.isActive else !it.isActive }

    val needsFundingCount: Int
        get() = jobs.count {
            it.status == JobStatus.ACCEPTED && paymentStatusMap[it.id] != PaymentStatus.PAID
        }

    /** COMPLETED by the worker, escrow funded, but not yet released. */
    val readyToReleaseCount: Int
        get() = jobs.count {
            it.status == JobStatus.COMPLETED &&
                    it.escrowFunded == true &&
                    paymentStatusMap[it.id] != PaymentStatus.RELEASED
        }

    /** Released but not yet reviewed. */
    val awaitingReviewCount: Int
        get() = jobs.count {
            it.status == JobStatus.COMPLETED &&
                    paymentStatusMap[it.id] == PaymentStatus.RELEASED &&
                    it.id !in reviewedJobIds
        }

    fun isReleased(jobId: String): Boolean = paymentStatusMap[jobId] == PaymentStatus.RELEASED
    fun isReviewed(jobId: String): Boolean = jobId in reviewedJobIds
}

class ClientJobsViewModel(
    private val getClientJobsUseCase: GetClientJobsUseCase,
    private val acceptCounterOfferUseCase: AcceptCounterOfferUseCase,
    private val cancelJobUseCase: CancelJobUseCase,
    private val fundEscrowUseCase: FundEscrowUseCase,
    private val checkPaymentStatusUseCase: CheckClientPaymentStatusUseCase,
    private val releaseEscrowUseCase: ReleaseEscrowUseCase,
    private val submitReviewUseCase: SubmitReviewUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientJobsUiState())
    val uiState: StateFlow<ClientJobsUiState> = _uiState.asStateFlow()

    private var clientId: String = ""
    private val paymentPollingJobs = mutableSetOf<String>()

    // Do NOT call this from init{} — clientId isn't known until login,
    // same reasoning as ChatListViewModel. Call from LaunchedEffect(currentUserId).
    fun loadJobs(clientId: String) {
        this.clientId = clientId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getClientJobsUseCase(clientId)
                .onSuccess { jobs ->
                    // Seed known payment statuses from the job data itself so gating
                    // is correct even on a fresh app launch, not only from live polls.
                    val seededMap = _uiState.value.paymentStatusMap.toMutableMap()
                    jobs.forEach { job -> job.paymentStatus?.let { seededMap[job.id] = it } }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        jobs = jobs,
                        paymentStatusMap = seededMap
                    )
                    // Resume polling for anything already ACCEPTED but not yet paid,
                    // e.g. if the app was killed mid-flow.
                    jobs.filter { it.status == JobStatus.ACCEPTED }
                        .forEach { pollPaymentStatus(it.id) }
                }
                .onFailure { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
        }
    }

    fun setFilter(filter: JobFilter) {
        _uiState.value = _uiState.value.copy(filter = filter)
    }

    fun refreshSilently() {
        if (clientId.isBlank()) return
        viewModelScope.launch {
            getClientJobsUseCase(clientId)
                .onSuccess { jobs -> _uiState.value = _uiState.value.copy(jobs = jobs) }
                .onFailure { /* silent */ }
        }
    }

    // ── Existing actions (unchanged) ─────────────────────

    fun acceptCounterOffer(jobId: String) = runAction(jobId) { acceptCounterOfferUseCase(jobId) }

    fun cancelJob(jobId: String) = runAction(jobId) { cancelJobUseCase(jobId) }

    private fun runAction(jobId: String, action: suspend () -> Result<Job>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(actionInProgressJobId = jobId, error = null)
            action()
                .onSuccess { updated ->
                    _uiState.value = _uiState.value.copy(
                        actionInProgressJobId = null,
                        jobs = _uiState.value.jobs.map { if (it.id == updated.id) updated else it }
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(actionInProgressJobId = null, error = e.message)
                }
        }
    }

    // ── Stage 1: Fund escrow (STK push) ──────────────────

    fun openFundingSheet(jobId: String) {
        _uiState.value = _uiState.value.copy(fundingJobId = jobId)
    }

    fun dismissFundingSheet() {
        _uiState.value = _uiState.value.copy(fundingJobId = null)
    }

    fun fundEscrow(jobId: String, phoneNumber: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                actionInProgressJobId = jobId, error = null, fundingJobId = null
            )
            fundEscrowUseCase(jobId, phoneNumber)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        actionInProgressJobId = null,
                        awaitingStkConfirmationJobId = jobId
                    )
                    updatePaymentStatus(jobId, PaymentStatus.PENDING)
                    pollPaymentStatus(jobId)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(actionInProgressJobId = null, error = e.message)
                }
        }
    }

    fun dismissStkWaitingDialog() {
        _uiState.value = _uiState.value.copy(awaitingStkConfirmationJobId = null)
    }

    private fun pollPaymentStatus(jobId: String) {
        if (jobId in paymentPollingJobs) return
        paymentPollingJobs.add(jobId)

        viewModelScope.launch {
            var attempts = 0
            val maxAttempts = 30 // ~5 minutes at 10s intervals

            while (attempts < maxAttempts) {
                delay(10_000)

                checkPaymentStatusUseCase(jobId)
                    .onSuccess { status ->
                        updatePaymentStatus(jobId, status)
                        if (status == PaymentStatus.PAID) {
                            paymentPollingJobs.remove(jobId)
                            _uiState.value = _uiState.value.copy(awaitingStkConfirmationJobId = null)
                            refreshSilently() // pick up IN_PROGRESS once backend flips it
                            return@launch
                        }
                    }
                    .onFailure { /* keep polling; transient network hiccup */ }

                attempts++
            }
            paymentPollingJobs.remove(jobId)
        }
    }

    private fun updatePaymentStatus(jobId: String, status: PaymentStatus) {
        val updated = _uiState.value.paymentStatusMap.toMutableMap()
        updated[jobId] = status
        _uiState.value = _uiState.value.copy(paymentStatusMap = updated)
    }

    // ── Stage 2: Job completed by worker → release funds ─

    fun releaseEscrow(jobId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(actionInProgressJobId = jobId, error = null)
            releaseEscrowUseCase(jobId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        actionInProgressJobId = null,
                        reviewPromptJobId = jobId
                    )
                    updatePaymentStatus(jobId, PaymentStatus.RELEASED)
                    refreshSilently()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(actionInProgressJobId = null, error = e.message)
                }
        }
    }

    // ── Stage 3: Review ───────────────────────────────────

    fun openReviewPrompt(jobId: String) {
        _uiState.value = _uiState.value.copy(reviewPromptJobId = jobId)
    }

    fun dismissReviewPrompt() {
        _uiState.value = _uiState.value.copy(reviewPromptJobId = null)
    }

    /**
     * workerProfileId: pass job.worker.id here. If job.worker.id turns out to be
     * the worker's USER id rather than PROFILE id, this needs a lookup step first
     * — verify by testing a real review submission (flagged open question).
     */
    fun submitReview(jobId: String, workerProfileId: String, rating: Int, comment: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(actionInProgressJobId = jobId, error = null)
            submitReviewUseCase(clientId, workerProfileId, jobId, rating, comment)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        actionInProgressJobId = null,
                        reviewPromptJobId = null,
                        reviewedJobIds = _uiState.value.reviewedJobIds + jobId
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(actionInProgressJobId = null, error = e.message)
                }
        }
    }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }

    fun clearState() {
        _uiState.value = ClientJobsUiState()
        paymentPollingJobs.clear()
        clientId = ""
    }
}