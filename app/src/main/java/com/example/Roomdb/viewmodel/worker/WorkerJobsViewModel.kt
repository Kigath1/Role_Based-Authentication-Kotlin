package com.example.Roomdb.viewmodel.worker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomdb.data.model.Job
import com.example.Roomdb.domain.usecases.worker.AcceptJobUseCase
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

data class WorkerJobsUiState(
    val isLoading: Boolean = false,
    val jobs: List<Job> = emptyList(),
    val filter: JobFilter = JobFilter.ACTIVE,
    val error: String? = null,
    val actionInProgressJobId: String? = null
) {
    val filteredJobs: List<Job>
        get() = jobs.filter { if (filter == JobFilter.ACTIVE) it.isActive else !it.isActive }

    // Feeds WorkerDashboardViewModel's badge — count of requests awaiting a response
    val pendingCount: Int
        get() = jobs.count { it.status.name == "PENDING" }
}

class WorkerJobsViewModel(
    private val getWorkerJobsUseCase: GetWorkerJobsUseCase,
    private val acceptJobUseCase: AcceptJobUseCase,
    private val rejectJobUseCase: RejectJobUseCase,
    private val counterOfferUseCase: CounterOfferUseCase,
    private val startJobUseCase: StartJobUseCase,
    private val completeJobUseCase: CompleteJobUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerJobsUiState())
    val uiState: StateFlow<WorkerJobsUiState> = _uiState.asStateFlow()

    private var workerUserId: String = ""

    // Same rule as ChatListViewModel / ClientJobsViewModel — never in init{}.
    fun loadJobs(workerUserId: String) {
        this.workerUserId = workerUserId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getWorkerJobsUseCase(workerUserId)
                .onSuccess { jobs -> _uiState.value = _uiState.value.copy(isLoading = false, jobs = jobs) }
                .onFailure { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
        }
    }

    fun setFilter(filter: JobFilter) {
        _uiState.value = _uiState.value.copy(filter = filter)
    }

    fun acceptJob(jobId: String) = runAction(jobId) { acceptJobUseCase(jobId) }
    fun rejectJob(jobId: String) = runAction(jobId) { rejectJobUseCase(jobId) }
    fun counterOffer(jobId: String, price: Double) = runAction(jobId) { counterOfferUseCase(jobId, price) }
    fun startJob(jobId: String) = runAction(jobId) { startJobUseCase(jobId) }
    fun completeJob(jobId: String) = runAction(jobId) { completeJobUseCase(jobId) }

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

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }

    fun clearState() {
        _uiState.value = WorkerJobsUiState()
        workerUserId = ""
    }
}