package com.example.Roomdb.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.Roomdb.ui.view.ClientHomeScreen
import com.example.Roomdb.ui.view.auth.LoginScreen
import com.example.Roomdb.ui.view.auth.RegistrationScreen
import com.example.Roomdb.ui.view.auth.VerifyEmailScreen
import com.example.Roomdb.ui.view.employer.ClientProfileSetupScreen
import com.example.Roomdb.ui.view.worker.WorkerHomeScreen
import com.example.Roomdb.ui.view.worker.WorkerOnboardingScreen
import com.example.Roomdb.viewmodel.auth.AuthViewModel
import com.example.Roomdb.viewmodel.auth.RegistrationViewModel
import com.example.Roomdb.viewmodel.employer.ChatListViewModel
import com.example.Roomdb.viewmodel.employer.ClientProfileSetupViewModel
import com.example.Roomdb.viewmodel.worker.WorkerOnboardingViewModel

@Composable
fun AppNavHost(
    authViewModel: AuthViewModel,
    clientHomeViewModel: com.example.Roomdb.viewmodel.employer.ClientHomeViewModel,
    chatListViewModel: ChatListViewModel,
    chatContent: @Composable (recipientId: String, recipientName: String, onBack: () -> Unit) -> Unit,
    registrationViewModel: RegistrationViewModel,
    clientProfileSetupViewModel: ClientProfileSetupViewModel,
    workerOnboardingViewModel: WorkerOnboardingViewModel
) {
    val backStack = rememberNavBackStack(ScreenKey.Splash)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {

            // ── SPLASH ────────────────────────────────────────────────────
            entry<ScreenKey.Splash> {
                LaunchedEffect(Unit) {
                    val isLoggedIn = authViewModel.checkAutoLogin()
                    val next = when {
                        !isLoggedIn -> ScreenKey.Login
                        authViewModel.getUserRole() == "WORKER" -> ScreenKey.WorkerHome
                        else -> ScreenKey.ClientHome
                    }
                    backStack.removeLastOrNull()
                    backStack.add(next)
                }
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            // ── LOGIN ─────────────────────────────────────────────────────
            entry<ScreenKey.Login> {
                val authState by authViewModel.authState.collectAsState()

                LaunchedEffect(authState.isLoggedIn) {
                    if (authState.isLoggedIn) {
                        val role = authState.role.uppercase()
                        backStack.removeLastOrNull()
                        backStack.add(
                            when (role) {
                                "WORKER" -> ScreenKey.WorkerHome
                                "CLIENT" -> ScreenKey.ClientHome
                                else -> ScreenKey.ClientHome
                            }
                        )
                    }
                }

                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = { /* handled by LaunchedEffect above */ },
                    onNavigateToRegistration = { backStack.add(ScreenKey.Registration) }
                )
            }

            // ── REGISTRATION ──────────────────────────────────────────────
            entry<ScreenKey.Registration> {
                RegistrationScreen(
                    viewModel = registrationViewModel,
                    onNavigateToLogin = { backStack.add(ScreenKey.Login) },
                    onNavigateToVerifyEmail = {
                        backStack.add(ScreenKey.VerifyEmail)
                    }
                )
            }

            // ── VERIFY EMAIL ──────────────────────────────────────────────
            entry<ScreenKey.VerifyEmail> {
                VerifyEmailScreen(
                    viewModel = registrationViewModel,
                    onVerificationSuccess = {
                        // Clear back stack and go to Login
                        backStack.clear()
                        backStack.add(ScreenKey.Login)
                    }
                )
            }
            // ── CLIENT PROFILE SETUP ──────────────────────────────────────
            entry<ScreenKey.ClientProfileSetup> {
                val currentUser by authViewModel.currentUser.collectAsState()
                val state by clientProfileSetupViewModel.state.collectAsState()

                LaunchedEffect(state.success) {
                    if (state.success) {
                        clientProfileSetupViewModel.consumeSuccess()
                        backStack.clear()
                        backStack.add(ScreenKey.ClientHome)
                    }
                }

                currentUser?.let { user ->
                    ClientProfileSetupScreen(
                        viewModel = clientProfileSetupViewModel,
                        email = user.email
                    )
                } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            // ── WORKER ONBOARDING ─────────────────────────────────────────
            entry<ScreenKey.WorkerOnboarding> {
                val currentUser by authViewModel.currentUser.collectAsState()
                val state by workerOnboardingViewModel.state.collectAsState()

                // Gate initialization on currentUser actually being populated —
                // loginSilently() is async, so this must react to the flow,
                // not read a synchronous getter on first composition.
                LaunchedEffect(currentUser) {
                    currentUser?.let { user ->
                        workerOnboardingViewModel.initialize(user.email, user.userId)
                    }
                }

                LaunchedEffect(state.done) {
                    if (state.done) {
                        backStack.clear()
                        backStack.add(ScreenKey.WorkerHome)
                    }
                }

                if (currentUser == null) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    WorkerOnboardingScreen(viewModel = workerOnboardingViewModel)
                }
            }

            // ── WORKER HOME ───────────────────────────────────────────────
            entry<ScreenKey.WorkerHome> {
                WorkerHomeScreen(
                    authViewModel = authViewModel,
                    onLogout = {
                        backStack.clear()
                        backStack.add(ScreenKey.Login)
                    }
                )
            }

            // ── CLIENT HOME ───────────────────────────────────────────────
            entry<ScreenKey.ClientHome> {
                val authState by authViewModel.authState.collectAsState()
                val currentUserId = authState.currentUserId

                LaunchedEffect(currentUserId) {
                    if (currentUserId.isNotBlank()) {
                        clientHomeViewModel.loadWorkers(forceRefresh = true)
                        chatListViewModel.loadConversations()
                    }
                }

                // ── Reload conversations every time ClientHome is composed ────────────
                // This catches the case where user returns from ChatScreen
                DisposableEffect(Unit) {
                    onDispose { }
                }

                ClientHomeScreen(
                    viewModel = clientHomeViewModel,
                    chatListViewModel = chatListViewModel,
                    onLogout = {
                        clientHomeViewModel.clearState()
                        chatListViewModel.clearState()
                        authViewModel.logout {
                            backStack.clear()
                            backStack.add(ScreenKey.Login)
                        }
                    },
                    onOpenChat = { recipientId, recipientName ->
                        backStack.add(ScreenKey.Chat(recipientId, recipientName))
                    }
                )
            }

            // ── CHAT — existing, unchanged ────────────────────────────────
            entry<ScreenKey.Chat> { key ->
                chatContent(key.recipientId, key.recipientName) {
                    backStack.removeLastOrNull()
                }
            }
        }
    )
}