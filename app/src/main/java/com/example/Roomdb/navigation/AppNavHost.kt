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
import com.example.Roomdb.ui.view.employer.ClientJobsScreen
import com.example.Roomdb.ui.view.worker.WorkerHomeScreen
import com.example.Roomdb.ui.view.worker.WorkerOnboardingScreen
import com.example.Roomdb.viewmodel.auth.AuthViewModel
import com.example.Roomdb.viewmodel.auth.PostLoginDestination
import com.example.Roomdb.viewmodel.auth.RegistrationViewModel
import com.example.Roomdb.viewmodel.common.chats.ChatListViewModel
import com.example.Roomdb.viewmodel.employer.ClientHomeViewModel
import com.example.Roomdb.viewmodel.employer.ClientJobsViewModel
import com.example.Roomdb.viewmodel.employer.ClientProfileViewModel
import com.example.Roomdb.viewmodel.employer.JobRequestViewModel
import com.example.Roomdb.viewmodel.worker.WorkerDashboardViewModel
import com.example.Roomdb.viewmodel.worker.WorkerJobsViewModel
import com.example.Roomdb.viewmodel.worker.WorkerOnboardingViewModel
import com.example.Roomdb.viewmodel.worker.WorkerProfileViewModel

@Composable
fun AppNavHost(
    authViewModel: AuthViewModel,
    clientHomeViewModel: ClientHomeViewModel,
    chatListViewModel: ChatListViewModel,
    chatContent: @Composable (recipientId: String, recipientName: String, onBack: () -> Unit) -> Unit,
    registrationViewModel: RegistrationViewModel,
    clientProfileViewModel: ClientProfileViewModel,
    workerOnboardingViewModel: WorkerOnboardingViewModel,
    workerDashboardViewModel: WorkerDashboardViewModel,
    workerProfileViewModel: WorkerProfileViewModel,
    clientJobsViewModel: ClientJobsViewModel,     // new
    workerJobsViewModel: WorkerJobsViewModel,     // new
    jobRequestViewModel: JobRequestViewModel      // new
) {
    val backStack = rememberNavBackStack(ScreenKey.Splash)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {

            // ── SPLASH ────────────────────────────────────────────────────
            entry<ScreenKey.Splash> {
                val authState by authViewModel.authState.collectAsState()

                LaunchedEffect(Unit) { authViewModel.checkAutoLogin() }

                LaunchedEffect(authState.isCheckingAutoLogin, authState.destination) {
                    if (!authState.isCheckingAutoLogin) {
                        val next = when {
                            !authState.isLoggedIn -> ScreenKey.Login
                            authState.destination == PostLoginDestination.WorkerHome -> ScreenKey.WorkerHome
                            authState.destination == PostLoginDestination.WorkerOnboarding -> ScreenKey.WorkerOnboarding
                            authState.destination == PostLoginDestination.ClientHome -> ScreenKey.ClientHome
                            else -> ScreenKey.Login
                        }
                        authViewModel.consumeDestination()
                        backStack.removeLastOrNull()
                        backStack.add(next)
                    }
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
                        backStack.add(if (role == "WORKER") ScreenKey.WorkerHome else ScreenKey.ClientHome)
                    }
                }

                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToRegistration = { backStack.add(ScreenKey.Registration) },
                    onNavigateToWorkerHome = { backStack.clear(); backStack.add(ScreenKey.WorkerHome) },
                    onNavigateToWorkerOnboarding = { backStack.clear(); backStack.add(ScreenKey.WorkerOnboarding) },
                    onNavigateToClientHome = { backStack.clear(); backStack.add(ScreenKey.ClientHome) }
                )
            }

            // ── REGISTRATION ──────────────────────────────────────────────
            entry<ScreenKey.Registration> {
                RegistrationScreen(
                    viewModel = registrationViewModel,
                    onNavigateToLogin = { backStack.add(ScreenKey.Login) },
                    onNavigateToVerifyEmail = { backStack.add(ScreenKey.VerifyEmail) }
                )
            }

            // ── VERIFY EMAIL ──────────────────────────────────────────────
            entry<ScreenKey.VerifyEmail> {
                VerifyEmailScreen(
                    viewModel = registrationViewModel,
                    onVerificationSuccess = { backStack.clear(); backStack.add(ScreenKey.Login) }
                )
            }

            // ── WORKER ONBOARDING ─────────────────────────────────────────
            entry<ScreenKey.WorkerOnboarding> {
                val currentUser by authViewModel.currentUser.collectAsState()
                val state by workerOnboardingViewModel.state.collectAsState()

                LaunchedEffect(currentUser) {
                    currentUser?.let { workerOnboardingViewModel.initialize(it.email, it.userId) }
                }
                LaunchedEffect(state.done) {
                    if (state.done) { backStack.clear(); backStack.add(ScreenKey.WorkerHome) }
                }

                if (currentUser == null) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else {
                    WorkerOnboardingScreen(viewModel = workerOnboardingViewModel)
                }
            }

            // ── WORKER HOME ───────────────────────────────────────────────
            entry<ScreenKey.WorkerHome> {
                val authState by authViewModel.authState.collectAsState()
                val currentUserId = authState.currentUserId

                LaunchedEffect(currentUserId) {
                    if (currentUserId.isNotBlank()) chatListViewModel.loadConversations()
                }

                WorkerHomeScreen(
                    authViewModel = authViewModel,
                    dashboardViewModel = workerDashboardViewModel,
                    chatListViewModel = chatListViewModel,
                    workerProfileViewModel = workerProfileViewModel,
                    workerJobsViewModel = workerJobsViewModel,
                    onOpenChat = { recipientId, recipientName -> backStack.add(ScreenKey.Chat(recipientId, recipientName)) },
                    onLogout = {
                        chatListViewModel.clearState()
                        workerDashboardViewModel.clearState()
                        workerProfileViewModel.clearState()
                        authViewModel.logout {
                            backStack.clear()
                            backStack.add(ScreenKey.Login)
                        }
                    }
                )
            }

            // ── CLIENT HOME ───────────────────────────────────────────────
            entry<ScreenKey.ClientHome> {
                val authState by authViewModel.authState.collectAsState()
                val currentUser by authViewModel.currentUser.collectAsState()
                val currentUserId = authState.currentUserId

                LaunchedEffect(currentUserId) {
                    if (currentUserId.isNotBlank()) {
                        clientHomeViewModel.loadWorkers(forceRefresh = true)
                        chatListViewModel.loadConversations()
                    }
                }

                ClientHomeScreen(
                    viewModel = clientHomeViewModel,
                    chatListViewModel = chatListViewModel,
                    clientProfileViewModel = clientProfileViewModel,
                    jobRequestViewModel = jobRequestViewModel,
                    currentUserId = currentUserId,
                    currentUserEmail = currentUser?.email ?: "",
                    onLogout = {
                        clientHomeViewModel.clearState()
                        chatListViewModel.clearState()
                        authViewModel.logout { backStack.clear(); backStack.add(ScreenKey.Login) }
                    },
                    onOpenChat = { recipientId, recipientName -> backStack.add(ScreenKey.Chat(recipientId, recipientName)) },
                    onOpenJobRequests = { backStack.add(ScreenKey.ClientJobs) }
                )
            }

            // ── CLIENT JOBS — pushed from "My Requests" ────────────────────
            entry<ScreenKey.ClientJobs> {
                val authState by authViewModel.authState.collectAsState()

                LaunchedEffect(authState.currentUserId) {
                    if (authState.currentUserId.isNotBlank()) {
                        clientJobsViewModel.loadJobs(authState.currentUserId)
                    }
                }

                ClientJobsScreen(
                    viewModel = clientJobsViewModel,
                    onBack = { backStack.removeLastOrNull() }
                )
            }

            // ── CHAT — shared, role-agnostic ────────────────────────────
            entry<ScreenKey.Chat> { key ->
                chatContent(key.recipientId, key.recipientName) { backStack.removeLastOrNull() }
            }
        }
    )
}