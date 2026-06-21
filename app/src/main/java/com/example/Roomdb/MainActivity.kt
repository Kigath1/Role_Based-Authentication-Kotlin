package com.example.Roomdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.Roomdb.navigation.AppNavHost
import com.example.Roomdb.ui.view.ClientHomeScreen
import com.example.Roomdb.ui.view.LoginScreen
import com.example.Roomdb.ui.view.WorkerHomeScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Pull the pre-built AuthViewModel from the Application class
        val app = application as TestKonnectApplication
        val authViewModel = app.authViewModel

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
                            ClientHomeScreen(authViewModel, onLogout)
                        }
                    )
                }
            }
        }
    }
}