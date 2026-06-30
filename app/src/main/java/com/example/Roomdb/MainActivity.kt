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
import com.example.Roomdb.navigation.AppNavHost
import com.example.Roomdb.ui.view.common.chats.ChatScreen
import com.example.Roomdb.viewmodel.auth.AuthViewModel
import com.example.Roomdb.viewmodel.auth.RegistrationViewModel
import com.example.Roomdb.viewmodel.common.chats.ChatListViewModel
import com.example.Roomdb.viewmodel.common.chats.ChatViewModel
import com.example.Roomdb.viewmodel.employer.ClientHomeViewModel
import com.example.Roomdb.viewmodel.employer.ClientProfileSetupViewModel
import com.example.Roomdb.viewmodel.worker.WorkerOnboardingViewModel

class MainActivity : ComponentActivity() {

    private val app by lazy { application as TestKonnectApplication }

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
                    app.secureStore
                ) as T
            }
        }
    }

    private val clientHomeViewModel: ClientHomeViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ClientHomeViewModel(app.getWorkersUseCase) as T
            }
        }
    }

    private val chatListViewModel: ChatListViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ChatListViewModel(
                    app.getRecentConversationsUseCase,
                    app.secureStore
                ) as T
            }
        }
    }

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

    private val clientProfileSetupViewModel: ClientProfileSetupViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ClientProfileSetupViewModel(app.createClientProfileUseCase) as T
            }
        }
    }

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
                        clientProfileSetupViewModel = clientProfileSetupViewModel,
                        workerOnboardingViewModel = workerOnboardingViewModel,

                        // ── CHAT — still external because ChatViewModel needs
                        //    a per-entry factory keyed on recipientId/recipientName ──
                        chatContent = { recipientId, recipientName, onBack ->
                            // ── Create a unique ViewModelStoreOwner for each chat ──────────────────
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