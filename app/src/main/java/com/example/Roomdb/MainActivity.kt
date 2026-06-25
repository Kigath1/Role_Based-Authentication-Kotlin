package com.example.Roomdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Roomdb.api.AuthTokenHolder
import com.example.Roomdb.navigation.AppNavHost
import com.example.Roomdb.ui.view.ClientHomeScreen
import com.example.Roomdb.ui.view.auth.LoginScreen
import com.example.Roomdb.ui.view.employer.chats.ChatScreen
import com.example.Roomdb.ui.view.worker.WorkerHomeScreen
import com.example.Roomdb.viewmodel.auth.AuthViewModel
import com.example.Roomdb.viewmodel.employer.ChatListViewModel
import com.example.Roomdb.viewmodel.employer.ChatViewModel
import com.example.Roomdb.viewmodel.employer.ClientHomeViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val app by lazy { application as TestKonnectApplication }



    private val authViewModel: AuthViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(
                    app.loginUseCase,
                    app.getCurrentUserUseCase,
                    app.getUserRoleUseCase,
                    app.logoutUseCase,
                    app.checkAuthStatusUseCase
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(
                        authViewModel = authViewModel,

                        // ── LOGIN ─────────────────────────────────────────────
                        loginContent = { onLoginSuccess ->
                            LoginScreen(authViewModel) { role ->
                                MainScope().launch {
                                    val token = app.secureStore.getAccessTokenOnce()
                                    if (token != null) AuthTokenHolder.token = token
                                    onLoginSuccess(role)   // ← navigate AFTER token is set
                                }
                            }
                        },

                        // ── WORKER HOME ───────────────────────────────────────
                        workerHomeContent = { onLogout ->
                            WorkerHomeScreen(authViewModel, onLogout)
                        },

                        // ── CLIENT HOME ───────────────────────────────────────
                        clientHomeContent = { onLogout, onOpenChat ->
                            ClientHomeScreen(
                                viewModel = clientHomeViewModel,
                                chatListViewModel = this@MainActivity.chatListViewModel,
                                onLogout = onLogout,
                                onOpenChat = onOpenChat
                            )
                        },

                        // ── CHAT ──────────────────────────────────────────────
                        chatContent = { recipientId, recipientName, onBack ->
                            val chatViewModel = viewModel<ChatViewModel>(
                                factory = object : ViewModelProvider.Factory {
                                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                        @Suppress("UNCHECKED_CAST")
                                        return ChatViewModel(
                                            app.getConversationUseCase,
                                            app.sendMessageUseCase,
                                            app.secureStore,
                                            recipientId,
                                            recipientName
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




//
//
//class MainActivity : ComponentActivity() {
//
//    private val app by lazy { application as TestKonnectApplication }
//
//    private val authViewModel: AuthViewModel by viewModels {
//        object : ViewModelProvider.Factory {
//            override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                @Suppress("UNCHECKED_CAST")
//                return AuthViewModel(
//                    loginUseCase = app.loginUseCase,
//                    getCurrentUserUseCase = app.getCurrentUserUseCase,
//                    getUserRoleUseCase = app.getUserRoleUseCase,
//                    logoutUseCase = app.logoutUseCase,
//                    checkAuthStatusUseCase = app.checkAuthStatusUseCase
//                ) as T
//            }
//        }
//    }
//
//    private val clientHomeViewModel: ClientHomeViewModel by viewModels {
//        object : ViewModelProvider.Factory {
//            override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                @Suppress("UNCHECKED_CAST")
//                return ClientHomeViewModel(app.getWorkersUseCase) as T
//            }
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//
//        setContent {
//            MaterialTheme {
//                Surface(modifier = Modifier.fillMaxSize()) {
//                    AppNavHost(
//                        authViewModel = authViewModel,
//                        loginContent = { onSuccess ->
//                            LoginScreen(authViewModel, onSuccess)
//                        },
//                        workerHomeContent = { onLogout ->
//                            WorkerHomeScreen(authViewModel, onLogout)
//                        },
//                        clientHomeContent = { onLogout ->
//                            ClientHomeScreen(viewModel = clientHomeViewModel, onLogout = onLogout)
//                        }
//                    )
//                }
//            }
//        }
//    }
//}