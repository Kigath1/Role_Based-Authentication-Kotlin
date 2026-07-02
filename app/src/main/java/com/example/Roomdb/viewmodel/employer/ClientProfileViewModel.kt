package com.example.Roomdb.viewmodel.employer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomdb.domain.usecases.employer.CreateClientProfileUseCase
import com.example.Roomdb.domain.usecases.employer.GetClientProfileUseCase
import com.example.Roomdb.domain.usecases.employer.UpdateClientProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ClientProfileUiState(
    val isLoading: Boolean = true,
    val profileExists: Boolean = false,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccessMessage: String? = null,

    // locked — never user-editable
    val id: String? = null,
    val email: String = "",

    // editable
    val fullName: String = "",
    val phoneNumber: String = "",

    // editable only while !profileExists; locked forever after first save
    val location: String = ""
)

class ClientProfileViewModel(
    private val getClientProfileUseCase: GetClientProfileUseCase,
    private val createClientProfileUseCase: CreateClientProfileUseCase,
    private val updateClientProfileUseCase: UpdateClientProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientProfileUiState())
    val uiState: StateFlow<ClientProfileUiState> = _uiState.asStateFlow()

    private var userId: String = ""

    fun loadProfile(userId: String, accountEmail: String) {
        this.userId = userId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            getClientProfileUseCase(userId)
                .onSuccess { profile ->
                    _uiState.value = if (profile != null) {
                        _uiState.value.copy(
                            isLoading = false,
                            profileExists = true,
                            isEditing = false,
                            id = profile.id,
                            email = profile.email ?: accountEmail,
                            fullName = profile.fullName,
                            phoneNumber = profile.phoneNumber?: "",
                            location = profile.location ?: ""
                        )
                    } else {
                        // No profile yet — this IS the creation flow now.
                        _uiState.value.copy(
                            isLoading = false,
                            profileExists = false,
                            isEditing = true,
                            email = accountEmail,
                            fullName = "",
                            phoneNumber = "",
                            location = ""
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
        }
    }

    fun onFullNameChange(value: String) { _uiState.value = _uiState.value.copy(fullName = value) }
    fun onPhoneNumberChange(value: String) { _uiState.value = _uiState.value.copy(phoneNumber = value) }

    fun onLocationChange(value: String) {
        if (!_uiState.value.profileExists) {
            _uiState.value = _uiState.value.copy(location = value)
        }
    }

    fun startEditing() { _uiState.value = _uiState.value.copy(isEditing = true, error = null) }

    fun cancelEditing() = loadProfile(userId, _uiState.value.email) // discards unsaved edits

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
    fun consumeSaveSuccess() { _uiState.value = _uiState.value.copy(saveSuccessMessage = null) }

    fun save() {
        val state = _uiState.value
        if (state.fullName.isBlank() || state.phoneNumber.isBlank()) {
            _uiState.value = state.copy(error = "Full name and phone number are required.")
            return
        }
        if (!state.profileExists && state.location.isBlank()) {
            _uiState.value = state.copy(error = "Location is required.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)

            val result = if (!state.profileExists) {
                createClientProfileUseCase(state.email, state.fullName, state.phoneNumber, state.location)
            } else {
                updateClientProfileUseCase(userId, state.fullName, state.phoneNumber, state.location)
            }

            result
                .onSuccess { profile ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        isEditing = false,
                        profileExists = true,
                        id = profile.id,
                        fullName = profile.fullName,
                        phoneNumber = profile.phoneNumber?: "",
                        location = profile.location ?: state.location,
                        saveSuccessMessage = "Profile saved."
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
                }
        }
    }
}