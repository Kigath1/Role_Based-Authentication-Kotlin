package com.example.Roomdb.viewmodel.worker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomdb.data.remote.model.worker.WorkerModels
import com.example.Roomdb.domain.usecases.worker.GetWorkerProfileUseCase
import com.example.Roomdb.domain.usecases.worker.UpdateWorkerProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WorkerProfileUiState(
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccessMessage: String? = null,
    val showReapprovalDialog: Boolean = false,

    val id: String = "",
    val email: String = "",
    val status: String = "",

    val fullName: String = "",
    val phoneNumber: String = "",
    val bio: String = "",
    val location: String = "",
    val experienceYears: String = "",
    val hourlyRate: String = "",
    val category: String = "",
    val skillsRaw: String = "",
    val preferredLocationsRaw: String = "",
    val availabilityWeekdays: Boolean = false,
    val availabilityWeekends: Boolean = false,
    val availabilityEvenings: Boolean = false,

    val workHistory: List<WorkerModels.WorkHistoryEntry> = emptyList(),
    val certifications: List<WorkerModels.Certification> = emptyList(),
    val profilePictureUrl: String? = null
)

class WorkerProfileViewModel(
    private val getWorkerProfileUseCase: GetWorkerProfileUseCase,
    private val updateWorkerProfileUseCase: UpdateWorkerProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerProfileUiState())
    val uiState: StateFlow<WorkerProfileUiState> = _uiState.asStateFlow()

    private var userId: String = ""

    fun loadProfile(userId: String) {
        this.userId = userId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            getWorkerProfileUseCase(userId)
                .onSuccess { profile ->
                    if (profile == null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "No profile found — complete onboarding first."
                        )
                        return@onSuccess
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isEditing = false,   // ← the fix — always return to view mode on (re)load
                        id = profile.id,
                        email = profile.email ?: "",
                        status = profile.status ?: "DRAFT",
                        fullName = profile.fullName,
                        phoneNumber = profile.phoneNumber,
                        bio = profile.bio ?: "",
                        location = profile.location ?: "",
                        experienceYears = profile.experienceYears?.toString() ?: "",
                        hourlyRate = profile.hourlyRate?.toString() ?: "",
                        category = profile.category ?: "",
                        skillsRaw = profile.skills.joinToString(", "),
                        preferredLocationsRaw = profile.preferredLocations.joinToString(", "),
                        availabilityWeekdays = profile.availabilityDetails?.weekdays ?: false,
                        availabilityWeekends = profile.availabilityDetails?.weekends ?: false,
                        availabilityEvenings = profile.availabilityDetails?.evenings ?: false,
                        workHistory = profile.workHistory,
                        certifications = profile.certifications,
                        profilePictureUrl = profile.profilePictureUrl
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
        }
    }

    fun onFullNameChange(v: String) { _uiState.value = _uiState.value.copy(fullName = v) }
    fun onPhoneNumberChange(v: String) { _uiState.value = _uiState.value.copy(phoneNumber = v) }
    fun onBioChange(v: String) { _uiState.value = _uiState.value.copy(bio = v) }
    fun onLocationChange(v: String) { _uiState.value = _uiState.value.copy(location = v) }
    fun onExperienceChange(v: String) { _uiState.value = _uiState.value.copy(experienceYears = v) }
    fun onHourlyRateChange(v: String) { _uiState.value = _uiState.value.copy(hourlyRate = v) }
    fun onCategoryChange(v: String) { _uiState.value = _uiState.value.copy(category = v) }
    fun onSkillsChange(v: String) { _uiState.value = _uiState.value.copy(skillsRaw = v) }
    fun onPreferredLocationsChange(v: String) { _uiState.value = _uiState.value.copy(preferredLocationsRaw = v) }
    fun onAvailabilityWeekdaysChange(v: Boolean) { _uiState.value = _uiState.value.copy(availabilityWeekdays = v) }
    fun onAvailabilityWeekendsChange(v: Boolean) { _uiState.value = _uiState.value.copy(availabilityWeekends = v) }
    fun onAvailabilityEveningsChange(v: Boolean) { _uiState.value = _uiState.value.copy(availabilityEvenings = v) }

    fun requestEdit() { _uiState.value = _uiState.value.copy(showReapprovalDialog = true) }
    fun confirmEditAfterWarning() {
        _uiState.value = _uiState.value.copy(showReapprovalDialog = false, isEditing = true, error = null)
    }
    fun dismissReapprovalWarning() { _uiState.value = _uiState.value.copy(showReapprovalDialog = false) }

    fun cancelEditing() = loadProfile(userId)
    fun clearState() {
        _uiState.value = WorkerProfileUiState()
    }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
    fun consumeSaveSuccess() { _uiState.value = _uiState.value.copy(saveSuccessMessage = null) }

    fun save() {
        val s = _uiState.value
        val experience = s.experienceYears.toIntOrNull()
        val rate = s.hourlyRate.toDoubleOrNull()

        if (s.fullName.isBlank() || s.phoneNumber.isBlank() || s.category.isBlank()) {
            _uiState.value = s.copy(error = "Full name, phone number and category are required.")
            return
        }
        if (experience == null || rate == null) {
            _uiState.value = s.copy(error = "Experience and hourly rate must be valid numbers.")
            return
        }

        val availabilitySummary = listOfNotNull(
            "Weekdays".takeIf { s.availabilityWeekdays },
            "Weekends".takeIf { s.availabilityWeekends },
            "Evenings".takeIf { s.availabilityEvenings }
        ).joinToString(", ")

        val request = WorkerModels.WorkerProfileRequest(
            fullName = s.fullName,
            phoneNumber = s.phoneNumber,
            bio = s.bio.ifBlank { null },
            location = s.location,
            experienceYears = experience,
            hourlyRate = rate,
            category = s.category,
            profilePictureUrl = s.profilePictureUrl,
            availabilityDetails = availabilitySummary.ifBlank { null },
            skills = s.skillsRaw.split(",").map { it.trim() }.filter { it.isNotBlank() },
            preferredLocations = s.preferredLocationsRaw.split(",").map { it.trim() }.filter { it.isNotBlank() },
            workHistory = s.workHistory,
            certifications = s.certifications
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            updateWorkerProfileUseCase(userId, request)
                .onSuccess { profile ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        isEditing = false,
                        status = profile.status ?: s.status,
                        saveSuccessMessage = "Profile updated — status: ${profile.status ?: "PENDING"}"
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
                }
        }
    }
}