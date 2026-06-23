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
import com.example.Roomdb.navigation.AppNavHost
import com.example.Roomdb.ui.view.ClientHomeScreen
import com.example.Roomdb.ui.view.auth.LoginScreen
import com.example.Roomdb.ui.view.worker.WorkerHomeScreen
import com.example.Roomdb.viewmodel.auth.AuthViewModel
import com.example.Roomdb.viewmodel.employer.ClientHomeViewModel

class MainActivity : ComponentActivity() {

    private val app by lazy { application as TestKonnectApplication }

    private val authViewModel: AuthViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(
                    loginUseCase = app.loginUseCase,
                    getCurrentUserUseCase = app.getCurrentUserUseCase,
                    getUserRoleUseCase = app.getUserRoleUseCase,
                    logoutUseCase = app.logoutUseCase,
                    checkAuthStatusUseCase = app.checkAuthStatusUseCase
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(
                        authViewModel = authViewModel,
                        loginContent = { onSuccess ->
                            LoginScreen(authViewModel, onSuccess)
                        },
                        workerHomeContent = { onLogout ->
                            WorkerHomeScreen(authViewModel, onLogout)
                        },
                        clientHomeContent = { onLogout ->
                            ClientHomeScreen(viewModel = clientHomeViewModel, onLogout = onLogout)
                        }
                    )
                }
            }
        }
    }
}