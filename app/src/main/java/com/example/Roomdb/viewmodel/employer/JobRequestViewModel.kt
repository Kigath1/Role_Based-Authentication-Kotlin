package com.example.Roomdb.viewmodel.employer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomdb.data.model.Job
import com.example.Roomdb.domain.usecases.employer.CreateJobRequestUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface JobRequestUiState {
    data object Idle : JobRequestUiState
    data object Loading : JobRequestUiState
    data class Success(val job: Job) : JobRequestUiState
    data class Error(val message: String) : JobRequestUiState
}

class JobRequestViewModel(
    private val createJobRequestUseCase: CreateJobRequestUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<JobRequestUiState>(JobRequestUiState.Idle)
    val state: StateFlow<JobRequestUiState> = _state.asStateFlow()

    fun submit(
        clientId: String,
        workerUserId: String, // caller must pass worker.userId, NOT worker.id
        description: String,
        location: String,
        scheduledDate: String,
        budget: Double
    ) {
        viewModelScope.launch {
            _state.value = JobRequestUiState.Loading
            createJobRequestUseCase(clientId, workerUserId, description, location, scheduledDate, budget)
                .onSuccess { job -> _state.value = JobRequestUiState.Success(job) }
                .onFailure { e -> _state.value = JobRequestUiState.Error(e.toString()) }
        }
    }

    fun reset() { _state.value = JobRequestUiState.Idle }
}