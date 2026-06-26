package com.example.Roomdb.viewmodel.auth


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomdb.domain.usecases.auth.RegisterUseCase
import com.example.Roomdb.domain.usecases.auth.ResendVerificationUseCase
import com.example.Roomdb.domain.usecases.auth.VerifyEmailUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val registerUseCase: RegisterUseCase,
    private val verifyEmailUseCase: VerifyEmailUseCase,
    private val resendVerificationUseCase: ResendVerificationUseCase
) : ViewModel() {

    data class UiState(
        val username: String = "",
        val email: String = "",
        val password: String = "",
        val firstName: String = "",
        val secondName: String = "",
        val selectedRole: String = "Client",      // "Client" or "Worker" — matches API
        val isLoading: Boolean = false,
        val error: String? = null,
        // Registration success → navigate to VerifyEmail
        val registrationSuccess: Boolean = false,
        // Verification success → navigate based on role
        val verificationSuccess: Boolean = false,
        val resendSuccess: Boolean = false,

        val loadingMessage: String = ""
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    // Temporarily held in-memory for the verify → profile-setup handoff.
    // Lives only as long as the ViewModel (i.e. the Activity) — safe for this flow.
    private var pendingEmail: String = ""
    private var pendingPassword: String = ""    // needed for auto-login after verification
    private var pendingRole: String = "Client"

    fun onUsernameChange(v: String)   { _state.value = _state.value.copy(username = v, error = null) }
    fun onEmailChange(v: String)      { _state.value = _state.value.copy(email = v, error = null) }
    fun onPasswordChange(v: String)   { _state.value = _state.value.copy(password = v, error = null) }
    fun onFirstNameChange(v: String)  { _state.value = _state.value.copy(firstName = v, error = null) }
    fun onSecondNameChange(v: String) { _state.value = _state.value.copy(secondName = v, error = null) }
    fun onRoleChange(v: String)       { _state.value = _state.value.copy(selectedRole = v, error = null) }

    fun register() {
        val s = _state.value
        if (s.username.isBlank() || s.email.isBlank() || s.password.isBlank()
            || s.firstName.isBlank() || s.secondName.isBlank()
        ) {
            _state.value = s.copy(error = "All fields are required")
            return
        }
        _state.value = s.copy(isLoading = true, error = null, loadingMessage = "")

        viewModelScope.launch {
            // Show cold-start message after 5s if still loading
            launch {
                delay(5000)
                if (_state.value.isLoading) {
                    _state.value = _state.value.copy(
                        loadingMessage = "Server is waking up, please wait…"
                    )
                }
            }
            val result = registerUseCase(
                s.username, s.email, s.password,
                s.firstName, s.secondName, s.selectedRole
            )
            if (result.isSuccess) {
                pendingEmail = s.email
                pendingPassword = s.password
                pendingRole = s.selectedRole
                _state.value = s.copy(
                    isLoading = false,
                    loadingMessage = "",
                    registrationSuccess = true
                )
            } else {
                _state.value = s.copy(
                    isLoading = false,
                    loadingMessage = "",
                    error = result.exceptionOrNull()?.message ?: "Registration failed"
                )
            }
        }
    }

    fun verifyEmail(token: String) {
        val email = pendingEmail.takeIf { it.isNotBlank() } ?: _state.value.email
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = verifyEmailUseCase(token, email)
            _state.value = if (result.isSuccess) {
                _state.value.copy(isLoading = false, verificationSuccess = true)
            } else {
                _state.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Verification failed"
                )
            }
        }
    }

    fun resendVerification() {
        val email = pendingEmail.takeIf { it.isNotBlank() } ?: _state.value.email
        viewModelScope.launch {
            resendVerificationUseCase(email)
            _state.value = _state.value.copy(resendSuccess = true)
        }
    }

    fun consumeRegistrationSuccess() { _state.value = _state.value.copy(registrationSuccess = false) }
    fun consumeVerificationSuccess() { _state.value = _state.value.copy(verificationSuccess = false) }

    // Called by AppNavHost after verification to trigger auto-login
    fun getPendingCredentials() = Triple(pendingEmail, pendingPassword, pendingRole)
}