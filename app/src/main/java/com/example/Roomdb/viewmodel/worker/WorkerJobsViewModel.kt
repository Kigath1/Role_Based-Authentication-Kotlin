package com.example.Roomdb.viewmodel.worker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomdb.data.model.Job
import com.example.Roomdb.data.model.JobStatus
import com.example.Roomdb.data.model.PaymentStatus
import com.example.Roomdb.domain.usecases.worker.AcceptJobUseCase
import com.example.Roomdb.domain.usecases.worker.CheckPaymentStatusUseCase
import com.example.Roomdb.domain.usecases.worker.CompleteJobUseCase
import com.example.Roomdb.domain.usecases.worker.CounterOfferUseCase
import com.example.Roomdb.domain.usecases.worker.GetWorkerJobsUseCase
import com.example.Roomdb.domain.usecases.worker.RejectJobUseCase
import com.example.Roomdb.domain.usecases.worker.StartJobUseCase
import com.example.Roomdb.viewmodel.employer.JobFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class WorkerJobsUiState(
    val isLoading: Boolean = false,
    val jobs: List<Job> = emptyList(),
    val filter: JobFilter = JobFilter.ACTIVE,
    val error: String? = null,
    val actionInProgressJobId: String? = null,
    val paymentStatusMap: Map<String, PaymentStatus> = emptyMap(),
    val showPaymentWaitingDialog: String? = null
) {
    val filteredJobs: List<Job>
        get() = jobs.filter { if (filter == JobFilter.ACTIVE) it.isActive else !it.isActive }

    val pendingCount: Int
        get() = jobs.count { it.status == JobStatus.PENDING }

    val waitingForPaymentCount: Int
        get() = jobs.count { it.isWaitingForPayment }
}

class WorkerJobsViewModel(
    private val getWorkerJobsUseCase: GetWorkerJobsUseCase,
    private val acceptJobUseCase: AcceptJobUseCase,
    private val rejectJobUseCase: RejectJobUseCase,
    private val counterOfferUseCase: CounterOfferUseCase,
    private val startJobUseCase: StartJobUseCase,
    private val completeJobUseCase: CompleteJobUseCase,
    private val checkPaymentStatusUseCase: CheckPaymentStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerJobsUiState())
    val uiState: StateFlow<WorkerJobsUiState> = _uiState.asStateFlow()

    private var workerUserId: String = ""
    private val paymentPollingJobs = mutableSetOf<String>()

    fun loadJobs(workerUserId: String) {
        this.workerUserId = workerUserId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getWorkerJobsUseCase(workerUserId)
                .onSuccess { jobs ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        jobs = jobs
                    )
                    startPaymentPolling(jobs)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
        }
    }

    fun setFilter(filter: JobFilter) {
        _uiState.value = _uiState.value.copy(filter = filter)
    }

    fun acceptJob(jobId: String) = runAction(jobId) {
        acceptJobUseCase(jobId)
    }

    fun rejectJob(jobId: String) = runAction(jobId) {
        rejectJobUseCase(jobId)
    }

    fun counterOffer(jobId: String, price: Double) = runAction(jobId) {
        counterOfferUseCase(jobId, price)
    }

    fun startJob(jobId: String) = runAction(jobId) {
        startJobUseCase(jobId)
    }

    fun completeJob(jobId: String) = runAction(jobId) {
        completeJobUseCase(jobId)
    }

    fun dismissPaymentWaitingDialog() {
        _uiState.value = _uiState.value.copy(showPaymentWaitingDialog = null)
    }

    private fun startPaymentPolling(jobs: List<Job>) {
        jobs.filter { it.status == JobStatus.ACCEPTED }.forEach { job ->
            if (job.id !in paymentPollingJobs) {
                pollPaymentStatus(job.id)
            }
        }
    }

    private fun pollPaymentStatus(jobId: String) {
        if (jobId in paymentPollingJobs) return
        paymentPollingJobs.add(jobId)

        viewModelScope.launch {
            var attempts = 0
            val maxAttempts = 30 // Poll for 5 minutes (10 seconds * 30)

            while (attempts < maxAttempts) {
                delay(10000) // Check every 10 seconds

                checkPaymentStatusUseCase(jobId)
                    .onSuccess { paymentStatus ->
                        updatePaymentStatus(jobId, paymentStatus)

                        // If payment is confirmed (PAID), stop polling and refresh job
                        if (paymentStatus == PaymentStatus.PAID) {
                            paymentPollingJobs.remove(jobId)
                            refreshJob(jobId)
                            break
                        }
                    }
                    .onFailure {
                        attempts++
                    }

                attempts++
            }

            paymentPollingJobs.remove(jobId)
        }
    }

    private fun updatePaymentStatus(jobId: String, paymentStatus: PaymentStatus) {
        val currentState = _uiState.value
        val updatedMap = currentState.paymentStatusMap.toMutableMap()
        updatedMap[jobId] = paymentStatus

        _uiState.value = currentState.copy(
            paymentStatusMap = updatedMap
        )
    }

    private fun refreshJob(jobId: String) {
        viewModelScope.launch {
            getWorkerJobsUseCase(workerUserId)
                .onSuccess { jobs ->
                    _uiState.value = _uiState.value.copy(jobs = jobs)
                }
                .onFailure { /* Handle error silently */ }
        }
    }

    private fun runAction(jobId: String, action: suspend () -> Result<Job>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                actionInProgressJobId = jobId,
                error = null
            )

            action()
                .onSuccess { updated ->
                    // If job is accepted, show waiting dialog and start polling
                    val showDialog = updated.status == JobStatus.ACCEPTED

                    _uiState.value = _uiState.value.copy(
                        actionInProgressJobId = null,
                        jobs = _uiState.value.jobs.map { if (it.id == updated.id) updated else it },
                        showPaymentWaitingDialog = if (showDialog) jobId else null
                    )

                    if (updated.status == JobStatus.ACCEPTED) {
                        pollPaymentStatus(jobId)
                    }
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        actionInProgressJobId = null,
                        error = e.message
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearState() {
        _uiState.value = WorkerJobsUiState()
        paymentPollingJobs.clear()
        workerUserId = ""
    }
}