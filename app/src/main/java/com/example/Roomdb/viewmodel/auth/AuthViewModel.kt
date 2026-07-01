package com.example.Roomdb.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomdb.api.AuthTokenHolder
import com.example.Roomdb.data.model.UserProfile
import com.example.Roomdb.domain.usecases.auth.CheckAuthStatusUseCase
import com.example.Roomdb.domain.usecases.auth.GetCurrentUserUseCase
import com.example.Roomdb.domain.usecases.auth.GetUserRoleUseCase
import com.example.Roomdb.domain.usecases.auth.LoginUseCase
import com.example.Roomdb.domain.usecases.auth.LogoutUseCase
import com.example.Roomdb.domain.usecases.employer.CheckClientProfileExistsUseCase
import com.example.Roomdb.domain.usecases.worker.CheckWorkerProfileExistsUseCase
import com.example.Roomdb.data.local.SecureTokenDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface PostLoginDestination {
    data object WorkerHome : PostLoginDestination
    data object WorkerOnboarding : PostLoginDestination
    data object ClientHome : PostLoginDestination
    data object ClientProfileSetup : PostLoginDestination
}

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getUserRoleUseCase: GetUserRoleUseCase,
    private val checkAuthStatusUseCase: CheckAuthStatusUseCase,
    private val checkWorkerProfileExistsUseCase: CheckWorkerProfileExistsUseCase,
    private val checkClientProfileExistsUseCase: CheckClientProfileExistsUseCase,
    private val secureStore: SecureTokenDataStore
) : ViewModel() {

    data class AuthState(
        val isLoggedIn: Boolean = false,
        val role: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val currentUserId: String = "",
        val currentUserEmail: String = "",
        val destination: PostLoginDestination? = null,
        val isCheckingAutoLogin: Boolean = true  // NEW: to show splash loading
    )

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser

    // ─── LOGIN ────────────────────────────────────────────────────────────
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.update { it.copy(error = "Email and password are required") }
            return
        }
        _authState.update { it.copy(isLoading = true, error = null, destination = null) }
        viewModelScope.launch {
            val result = loginUseCase(email, password)
            result.onSuccess { loginData ->
                // ── Inject token ──────────────────────────────────────────
                val token = secureStore.getAccessTokenOnce()
                if (token != null) {
                    AuthTokenHolder.token = token
                }

                val user = getCurrentUserUseCase()
                val role = getUserRoleUseCase()
                _currentUser.value = user

                // Determine destination based on role and profile existence
                val destination = when (role?.uppercase()) {
                    "WORKER" -> {
                        val userId = user?.userId ?: ""
                        val exists = runCatching {
                            checkWorkerProfileExistsUseCase(userId).getOrDefault(false)
                        }.getOrDefault(false)
                        if (exists) PostLoginDestination.WorkerHome
                        else PostLoginDestination.WorkerOnboarding
                    }
                    "CLIENT" -> {
                        val userId = user?.userId ?: ""
                        val exists = runCatching {
                            checkClientProfileExistsUseCase(userId).getOrDefault(false)
                        }.getOrDefault(false)
                        if (exists) PostLoginDestination.ClientHome
                        else PostLoginDestination.ClientProfileSetup
                    }
                    else -> null // shouldn't happen
                }

                _authState.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        role = role ?: "",
                        currentUserId = user?.userId ?: "",
                        currentUserEmail = user?.email ?: loginData.email,
                        error = null,
                        destination = destination
                    )
                }
            }.onFailure { error ->
                _authState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Login failed"
                    )
                }
            }
        }
    }

    // ─── CHECK AUTO LOGIN ────────────────────────────────────────────────
    suspend fun checkAutoLogin(): Boolean {
        return try {
            val isValid = checkAuthStatusUseCase()
            if (isValid) {
                val token = secureStore.getAccessTokenOnce()
                if (token != null) {
                    AuthTokenHolder.token = token
                }

                val user = getCurrentUserUseCase()
                val role = getUserRoleUseCase()
                _currentUser.value = user

                // Determine destination
                val destination = when (role?.uppercase()) {
                    "WORKER" -> {
                        val userId = user?.userId ?: ""
                        val exists = runCatching {
                            checkWorkerProfileExistsUseCase(userId).getOrDefault(false)
                        }.getOrDefault(false)
                        if (exists) PostLoginDestination.WorkerHome
                        else PostLoginDestination.WorkerOnboarding
                    }
                    "CLIENT" -> {
                        val userId = user?.userId ?: ""
                        val exists = runCatching {
                            checkClientProfileExistsUseCase(userId).getOrDefault(false)
                        }.getOrDefault(false)
                        if (exists) PostLoginDestination.ClientHome
                        else PostLoginDestination.ClientProfileSetup
                    }
                    else -> null
                }

                _authState.update {
                    it.copy(
                        isLoggedIn = true,
                        role = role ?: "",
                        currentUserId = user?.userId ?: "",
                        currentUserEmail = user?.email ?: "",
                        destination = destination,
                        isCheckingAutoLogin = false
                    )
                }
            } else {
                _authState.update { it.copy(isCheckingAutoLogin = false) }
            }
            isValid
        } catch (e: Exception) {
            _authState.update {
                it.copy(
                    error = e.message ?: "Auto-login failed",
                    isCheckingAutoLogin = false
                )
            }
            false
        }
    }

    // ─── SILENT LOGIN (for refresh) ──────────────────────────────────────
    fun loginSilently(email: String, password: String) {
        viewModelScope.launch {
            loginUseCase(email, password)
            val token = secureStore.getAccessTokenOnce()
            if (token != null) AuthTokenHolder.token = token
            _currentUser.value = getCurrentUserUseCase()
        }
    }

    // ─── HELPERS ──────────────────────────────────────────────────────────
    fun getCurrentUserId(): String = _authState.value.currentUserId
    fun getCurrentUserEmail(): String = _authState.value.currentUserEmail
    fun getUserRole(): String = _authState.value.role

    fun consumeDestination() {
        _authState.update { it.copy(destination = null) }
    }

    // ─── LOGOUT ───────────────────────────────────────────────────────────
    fun logout(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            logoutUseCase()
            _authState.value = AuthState() // resets everything incl. isCheckingAutoLogin
            _currentUser.value = null
            onComplete()
        }
    }
}