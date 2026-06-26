package com.example.Roomdb.viewmodel.worker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomdb.data.remote.model.WorkerModels
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.Roomdb.domain.usecases.worker.*
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WorkerOnboardingViewModel(
    private val createWorkerProfileUseCase: CreateWorkerProfileUseCase,
    private val updateWorkerProfileUseCase: UpdateWorkerProfileUseCase,
    private val uploadDocumentUseCase: UploadDocumentUseCase
) : ViewModel() {

    enum class Step { BASIC_INFO, ADDITIONAL_INFO, DOCUMENT_UPLOAD, DONE }

    data class UiState(
        val step: Step = Step.BASIC_INFO,
        // Basic Info
        val fullName: String = "",
        val phoneNumber: String = "",
        val bio: String = "",
        val location: String = "",
        val experienceYears: String = "",     // String for TextField, parse on submit
        val hourlyRate: String = "",
        val category: String = "",
        val skillsRaw: String = "",           // comma-separated for simple input
        // Additional Info
        val availabilityDetails: String = "",
        val preferredLocationsRaw: String = "",
        // Document upload
        val uploadedDocumentName: String? = null,
        // Shared
        val profileId: String? = null,        // set after first createProfile succeeds
        val isLoading: Boolean = false,
        val error: String? = null,
        val done: Boolean = false
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    private var email: String = ""
    private var userId: String = ""

    fun initialize(email: String, userId: String) {
        this.email = email
        this.userId = userId
    }

    // ── Field updaters ────────────────────────────────────────────────────
    fun onFullNameChange(v: String)         { _state.value = _state.value.copy(fullName = v, error = null) }
    fun onPhoneChange(v: String)            { _state.value = _state.value.copy(phoneNumber = v, error = null) }
    fun onBioChange(v: String)              { _state.value = _state.value.copy(bio = v) }
    fun onLocationChange(v: String)         { _state.value = _state.value.copy(location = v, error = null) }
    fun onExperienceChange(v: String)       { _state.value = _state.value.copy(experienceYears = v, error = null) }
    fun onHourlyRateChange(v: String)       { _state.value = _state.value.copy(hourlyRate = v, error = null) }
    fun onCategoryChange(v: String)         { _state.value = _state.value.copy(category = v, error = null) }
    fun onSkillsChange(v: String)           { _state.value = _state.value.copy(skillsRaw = v) }
    fun onAvailabilityChange(v: String)     { _state.value = _state.value.copy(availabilityDetails = v) }
    fun onPreferredLocationsChange(v: String) { _state.value = _state.value.copy(preferredLocationsRaw = v) }

    fun submitBasicInfo() {
        val s = _state.value
        val exp = s.experienceYears.toIntOrNull()
        val rate = s.hourlyRate.toDoubleOrNull()
        if (s.fullName.isBlank() || s.phoneNumber.isBlank() || s.location.isBlank()
            || s.category.isBlank() || s.skillsRaw.isBlank() || exp == null || rate == null
        ) {
            _state.value = s.copy(error = "Please fill all required fields with valid values")
            return
        }
        _state.value = s.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val request = buildRequest(s, exp, rate)
            val result = if (s.profileId != null) {
                updateWorkerProfileUseCase(userId, request)
            } else {
                createWorkerProfileUseCase(email, request)
            }
            if (result.isSuccess) {
                val profileId = result.getOrNull()?.id ?: s.profileId
                _state.value = s.copy(
                    isLoading = false,
                    profileId = profileId,
                    step = Step.ADDITIONAL_INFO
                )
            } else {
                _state.value = s.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Failed to save profile"
                )
            }
        }
    }

    fun submitAdditionalInfo() {
        val s = _state.value
        _state.value = s.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val exp = s.experienceYears.toIntOrNull() ?: 0
            val rate = s.hourlyRate.toDoubleOrNull() ?: 0.0
            val request = buildRequest(s, exp, rate)
            val result = updateWorkerProfileUseCase(userId, request)
            _state.value = if (result.isSuccess) {
                s.copy(isLoading = false, step = Step.DOCUMENT_UPLOAD)
            } else {
                s.copy(isLoading = false, error = result.exceptionOrNull()?.message)
            }
        }
    }

    fun uploadDocument(fileBytes: ByteArray, type: String, name: String, mimeType: String) {
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = uploadDocumentUseCase(userId, type, name, fileBytes, mimeType)
            _state.value = if (result.isSuccess) {
                _state.value.copy(
                    isLoading = false,
                    uploadedDocumentName = result.getOrNull()?.name,
                    step = Step.DONE,
                    done = true
                )
            } else {
                _state.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Upload failed"
                )
            }
        }
    }

    fun skipDocumentUpload() {
        _state.value = _state.value.copy(step = Step.DONE, done = true)
    }

    fun goBack() {
        _state.value = when (_state.value.step) {
            Step.ADDITIONAL_INFO -> _state.value.copy(step = Step.BASIC_INFO)
            Step.DOCUMENT_UPLOAD -> _state.value.copy(step = Step.ADDITIONAL_INFO)
            else -> _state.value
        }
    }

    private fun buildRequest(s: UiState, exp: Int, rate: Double) =
        WorkerModels.WorkerProfileRequest(
            fullName = s.fullName,
            phoneNumber = s.phoneNumber,
            bio = s.bio.takeIf { it.isNotBlank() },
            location = s.location,
            experienceYears = exp,
            hourlyRate = rate,
            category = s.category,
            availabilityDetails = s.availabilityDetails.takeIf { it.isNotBlank() },
            skills = s.skillsRaw.split(",").map { it.trim() }.filter { it.isNotBlank() },
            preferredLocations = s.preferredLocationsRaw.split(",").map { it.trim() }.filter { it.isNotBlank() }
        )
}