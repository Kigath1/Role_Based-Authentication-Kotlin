package com.example.Roomdb.viewmodel.worker

import androidx.lifecycle.ViewModel
import com.example.Roomdb.data.local.SecureTokenDataStore
import com.example.Roomdb.data.remote.model.WorkerModels
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class WorkerDashboardUiState(
    val name: String = "",
    val profilePictureUrl: String? = null,
    val pendingJobRequestsCount: Int = 0,
    val profileCompletionPercent: Int = 0
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

    private fun calculateCompletion(profile: WorkerModels.WorkerProfileResponse): Int {
        val checks = listOf(
            profile.profilePictureUrl?.isNotBlank() == true,
            profile.bio?.isNotBlank() == true,
            profile.skills.isNotEmpty(),
            profile.preferredLocations.isNotEmpty(),
            profile.availabilityDetails?.isNotBlank() == true,
            profile.workHistory.isNotEmpty(),
            profile.certifications.isNotEmpty(),
//            profile.hourlyRate >= 0
        )
        val completed = checks.count { it }
        return (completed * 100) / checks.size
    }
}