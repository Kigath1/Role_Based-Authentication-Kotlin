package com.example.Roomdb.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomdb.data.model.UserProfile
import com.example.Roomdb.domain.usecases.auth.CheckAuthStatusUseCase
import com.example.Roomdb.domain.usecases.auth.GetCurrentUserUseCase
import com.example.Roomdb.domain.usecases.auth.GetUserRoleUseCase
import com.example.Roomdb.domain.usecases.auth.LoginUseCase
import com.example.Roomdb.domain.usecases.auth.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getUserRoleUseCase: GetUserRoleUseCase,
    private val checkAuthStatusUseCase: CheckAuthStatusUseCase
) : ViewModel() {

    data class AuthState(
        val isLoggedIn: Boolean = false,
        val role: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val currentUserId: String = "",
        val currentUserEmail: String = ""
    )

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    // Single source of truth for the logged-in user's full profile.
    // Populated by login(), checkAutoLogin(), AND loginSilently() —
    // any screen needing the full UserProfile collects this, not authState.
    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.update { it.copy(error = "Email and password are required") }
            return
        }
        _authState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val result = loginUseCase(email, password)
            if (result.isSuccess) {
                val user = getCurrentUserUseCase()
                val role = getUserRoleUseCase()
                _currentUser.value = user
                _authState.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        role = role ?: "",
                        currentUserId = user?.userId ?: "",
                        currentUserEmail = user?.email ?: email,
                        error = null
                    )
                }
            } else {
                _authState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Login failed"
                    )
                }
            }
        }
    }

    suspend fun checkAutoLogin(): Boolean {
        return try {
            val isValid = checkAuthStatusUseCase()
            if (isValid) {
                val user = getCurrentUserUseCase()
                val role = getUserRoleUseCase()
                _currentUser.value = user
                _authState.update {
                    it.copy(
                        isLoggedIn = true,
                        role = role ?: "",
                        currentUserId = user?.userId ?: "",
                        currentUserEmail = user?.email ?: ""
                    )
                }
            }
            isValid
        } catch (e: Exception) {
            false
        }
    }

    // Called by AppNavHost after email verification, before ClientProfileSetup /
    // WorkerOnboarding fire their auth-required calls. Populates _currentUser
    // but deliberately does NOT flip isLoggedIn — the user is still mid-onboarding.
    fun loginSilently(email: String, password: String) {
        viewModelScope.launch {
            loginUseCase(email, password)
            _currentUser.value = getCurrentUserUseCase()
        }
    }

    fun getCurrentUserId(): String = _authState.value.currentUserId
    fun getCurrentUserEmail(): String = _authState.value.currentUserEmail
    fun getUserRole(): String = _authState.value.role

    fun logout(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            logoutUseCase()
            _authState.value = AuthState()
            _currentUser.value = null
            onComplete()
        }
    }
}