package com.example.Roomdb.viewmodel.employer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.Roomdb.domain.usecases.employer.CreateClientProfileUseCase
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClientProfileSetupViewModel(
    private val createClientProfileUseCase: CreateClientProfileUseCase
) : ViewModel() {

    data class UiState(
        val fullName: String = "",
        val phoneNumber: String = "",
        val location: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val success: Boolean = false
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    fun onFullNameChange(v: String)    { _state.value = _state.value.copy(fullName = v, error = null) }
    fun onPhoneNumberChange(v: String) { _state.value = _state.value.copy(phoneNumber = v, error = null) }
    fun onLocationChange(v: String)    { _state.value = _state.value.copy(location = v, error = null) }

    fun createProfile(email: String) {
        val s = _state.value
        if (s.fullName.isBlank() || s.phoneNumber.isBlank() || s.location.isBlank()) {
            _state.value = s.copy(error = "All fields are required")
            return
        }
        _state.value = s.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = createClientProfileUseCase(email, s.fullName, s.phoneNumber, s.location)
            _state.value = if (result.isSuccess) {
                s.copy(isLoading = false, success = true)
            } else {
                s.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Profile creation failed")
            }
        }
    }

    fun consumeSuccess() { _state.value = _state.value.copy(success = false) }
}