package com.example.Roomdb.viewmodel.employer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomdb.data.model.Job
import com.example.Roomdb.domain.usecases.employer.AcceptCounterOfferUseCase
import com.example.Roomdb.domain.usecases.employer.CancelJobUseCase
import com.example.Roomdb.domain.usecases.employer.GetClientJobsUseCase
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
    val actionInProgressJobId: String? = null // disables buttons on just that card during a call
) {
    val filteredJobs: List<Job>
        get() = jobs.filter { if (filter == JobFilter.ACTIVE) it.isActive else !it.isActive }
}

class ClientJobsViewModel(
    private val getClientJobsUseCase: GetClientJobsUseCase,
    private val acceptCounterOfferUseCase: AcceptCounterOfferUseCase,
    private val cancelJobUseCase: CancelJobUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientJobsUiState())
    val uiState: StateFlow<ClientJobsUiState> = _uiState.asStateFlow()

    private var clientId: String = ""

    // Do NOT call this from init{} — clientId isn't known until login,
    // same reasoning as ChatListViewModel. Call from LaunchedEffect(currentUserId).
    fun loadJobs(clientId: String) {
        this.clientId = clientId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getClientJobsUseCase(clientId)
                .onSuccess { jobs -> _uiState.value = _uiState.value.copy(isLoading = false, jobs = jobs) }
                .onFailure { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
        }
    }

    fun setFilter(filter: JobFilter) {
        _uiState.value = _uiState.value.copy(filter = filter)
    }

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

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }

    fun clearState() {
        _uiState.value = ClientJobsUiState()
        clientId = ""
    }
}