package com.example.Roomdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.Roomdb.navigation.AppNavHost
import com.example.Roomdb.ui.view.common.chats.ChatScreen
import com.example.Roomdb.viewmodel.auth.AuthViewModel
import com.example.Roomdb.viewmodel.auth.RegistrationViewModel
import com.example.Roomdb.viewmodel.common.chats.ChatListViewModel
import com.example.Roomdb.viewmodel.common.chats.ChatViewModel
import com.example.Roomdb.viewmodel.employer.ClientHomeViewModel
import com.example.Roomdb.viewmodel.employer.ClientProfileViewModel
import com.example.Roomdb.viewmodel.employer.ClientJobsViewModel
import com.example.Roomdb.viewmodel.employer.JobRequestViewModel
import com.example.Roomdb.viewmodel.worker.WalletViewModel
import com.example.Roomdb.viewmodel.worker.WorkerDashboardViewModel
import com.example.Roomdb.viewmodel.worker.WorkerOnboardingViewModel
import com.example.Roomdb.viewmodel.worker.WorkerProfileViewModel
import com.example.Roomdb.viewmodel.worker.WorkerJobsViewModel

class MainActivity : ComponentActivity() {

    private val app by lazy { application as TestKonnectApplication }

    // ── Auth ──────────────────────────────────────────────────────────────
    private val authViewModel: AuthViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(
                    app.loginUseCase,
                    app.logoutUseCase,
                    app.getCurrentUserUseCase,
                    app.getUserRoleUseCase,
                    app.checkAuthStatusUseCase,
                    app.checkWorkerProfileExistsUseCase,
                    app.secureStore
                ) as T
            }
        }
    }

    // ── Client Home ──────────────────────────────────────────────────────
    private val clientHomeViewModel: ClientHomeViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ClientHomeViewModel(app.getWorkersUseCase) as T
            }
        }
    }

    // ── Chat ──────────────────────────────────────────────────────────────
    private val chatListViewModel: ChatListViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ChatListViewModel(app.getRecentConversationsUseCase, app.secureStore) as T
            }
        }
    }

    // ── Registration ─────────────────────────────────────────────────────
    private val registrationViewModel: RegistrationViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return RegistrationViewModel(
                    app.registerUseCase,
                    app.verifyEmailUseCase,
                    app.resendVerificationUseCase
                ) as T
            }
        }
    }

    // ── Client Profile ──────────────────────────────────────────────────
    private val clientProfileViewModel: ClientProfileViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ClientProfileViewModel(
                    app.getClientProfileUseCase,
                    app.createClientProfileUseCase,
                    app.updateClientProfileUseCase
                ) as T
            }
        }
    }

    // ── Worker Profile ──────────────────────────────────────────────────
    private val workerProfileViewModel: WorkerProfileViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return WorkerProfileViewModel(
                    app.getWorkerProfileUseCase,
                    app.updateWorkerProfileUseCase
                ) as T
            }
        }
    }

    // ── Worker Onboarding ──────────────────────────────────────────────
    private val workerOnboardingViewModel: WorkerOnboardingViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return WorkerOnboardingViewModel(
                    app.createWorkerProfileUseCase,
                    app.updateWorkerProfileUseCase,
                    app.uploadDocumentUseCase
                ) as T
            }
        }
    }

    // ── Worker Dashboard ────────────────────────────────────────────────
    private val workerDashboardViewModel: WorkerDashboardViewModel by viewModels {
        viewModelFactory {
            initializer {
                WorkerDashboardViewModel(
                    secureStore = app.secureStore,
                    getWorkerProfileUseCase = app.getWorkerProfileUseCase
                )
            }
        }
    }

    // ── Client Jobs ──────────────────────────────────────────────────────
    private val clientJobsViewModel: ClientJobsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ClientJobsViewModel(
                    app.getClientJobsUseCase,
                    app.acceptCounterOfferUseCase,
                    app.cancelJobUseCase
                ) as T
            }
        }
    }

    // ── Worker Jobs ──────────────────────────────────────────────────────
    private val workerJobsViewModel: WorkerJobsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return WorkerJobsViewModel(
                    app.getWorkerJobsUseCase,
                    app.acceptJobUseCase,
                    app.rejectJobUseCase,
                    app.counterOfferUseCase,
                    app.startJobUseCase,
                    app.completeJobUseCase,
                    app.checkPaymentStatusUseCase
                ) as T
            }
        }
    }

    // ── Job Request ──────────────────────────────────────────────────────
    private val jobRequestViewModel: JobRequestViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return JobRequestViewModel(app.createJobRequestUseCase) as T
            }
        }
    }

    // ── Wallet ──────────────────────────────────────────────────────────
    private val walletViewModel: WalletViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return WalletViewModel(
                    app.getWalletBalanceUseCase,
                    app.getWalletTransactionsUseCase,
                    app.withdrawFundsUseCase
                ) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(
                        authViewModel = authViewModel,
                        clientHomeViewModel = clientHomeViewModel,
                        chatListViewModel = chatListViewModel,
                        registrationViewModel = registrationViewModel,
                        clientProfileViewModel = clientProfileViewModel,
                        workerOnboardingViewModel = workerOnboardingViewModel,
                        workerDashboardViewModel = workerDashboardViewModel,
                        workerProfileViewModel = workerProfileViewModel,
                        clientJobsViewModel = clientJobsViewModel,
                        workerJobsViewModel = workerJobsViewModel,
                        jobRequestViewModel = jobRequestViewModel,
                        walletViewModel = walletViewModel,
                        chatContent = { recipientId, recipientName, onBack ->
                            val viewModelStoreOwner = remember(recipientId) {
                                object : ViewModelStoreOwner {
                                    override val viewModelStore = ViewModelStore()
                                }
                            }
                            val chatViewModel: ChatViewModel = viewModel(
                                viewModelStoreOwner = viewModelStoreOwner,
                                factory = object : ViewModelProvider.Factory {
                                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                        @Suppress("UNCHECKED_CAST")
                                        return ChatViewModel(
                                            getConversationUseCase = app.getConversationUseCase,
                                            sendMessageUseCase = app.sendMessageUseCase,
                                            markMessageAsReadUseCase = app.markMessageAsReadUseCase,
                                            secureStore = app.secureStore,
                                            recipientId = recipientId,
                                            recipientName = recipientName
                                        ) as T
                                    }
                                }
                            )
                            ChatScreen(viewModel = chatViewModel, onBack = onBack)
                        }
                    )
                }
            }
        }
    }
}