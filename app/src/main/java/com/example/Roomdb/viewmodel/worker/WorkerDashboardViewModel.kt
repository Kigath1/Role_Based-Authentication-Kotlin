package com.example.Roomdb.viewmodel.worker

import androidx.lifecycle.ViewModel
import com.example.Roomdb.data.local.SecureTokenDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class WorkerDashboardUiState(
    val name: String = "",
    val profilePictureUrl: String? = null,
    // Job requests wiring comes later — placeholder for now, per agreed sequencing
    val pendingJobRequestsCount: Int = 0
)

// Lightweight — does NOT own conversations or job request lists.
// Just enough to render the header + badge counts on the hub.
class WorkerDashboardViewModel(
    private val secureStore: SecureTokenDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerDashboardUiState())
    val uiState: StateFlow<WorkerDashboardUiState> = _uiState.asStateFlow()

    fun loadProfile(name: String, profilePictureUrl: String?) {
        _uiState.update { it.copy(name = name, profilePictureUrl = profilePictureUrl) }
    }

    fun clearState() {
        _uiState.value = WorkerDashboardUiState()
    }
}