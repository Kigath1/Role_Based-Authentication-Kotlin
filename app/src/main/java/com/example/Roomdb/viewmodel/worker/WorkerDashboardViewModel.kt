package com.example.Roomdb.viewmodel.worker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomdb.data.local.SecureTokenDataStore
import com.example.Roomdb.data.remote.model.WorkerModels
import com.example.Roomdb.domain.usecases.worker.GetWorkerProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WorkerDashboardUiState(
    val name: String = "",
    val profilePictureUrl: String? = null,
    val pendingJobRequestsCount: Int = 0,
    val profileCompletionPercent: Int = 0
)

class WorkerDashboardViewModel(
    private val secureStore: SecureTokenDataStore,
    private val getWorkerProfileUseCase: GetWorkerProfileUseCase   // new dependency
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerDashboardUiState())
    val uiState: StateFlow<WorkerDashboardUiState> = _uiState.asStateFlow()

    fun loadProfile(name: String, profilePictureUrl: String?) {
        _uiState.update { it.copy(name = name, profilePictureUrl = profilePictureUrl) }
    }

    fun loadCompletion(userId: String) {
        viewModelScope.launch {
            getWorkerProfileUseCase(userId).onSuccess { profile ->
                if (profile != null) {
                    _uiState.update { it.copy(profileCompletionPercent = calculateCompletion(profile)) }
                }
            }
        }
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
            profile.availabilityDetails != null,
            profile.workHistory.isNotEmpty(),
            profile.certifications.isNotEmpty(),
        )
        return (checks.count { it } * 100) / checks.size
    }
}