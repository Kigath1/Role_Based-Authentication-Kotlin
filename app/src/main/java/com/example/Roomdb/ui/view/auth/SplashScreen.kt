package com.example.Roomdb.ui.view.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.Roomdb.viewmodel.auth.AuthViewModel
import com.example.Roomdb.viewmodel.auth.PostLoginDestination

@Composable
fun SplashScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToWorkerHome: () -> Unit,
    onNavigateToWorkerOnboarding: () -> Unit,
    onNavigateToClientHome: () -> Unit,
    onNavigateToClientProfileSetup: () -> Unit,
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    // ── Trigger auto-login check on first composition ────────────────
    LaunchedEffect(Unit) {
        viewModel.checkAutoLogin()
    }

    // ── Navigate when destination is set ──────────────────────────────
    LaunchedEffect(authState.destination) {
        when (authState.destination) {
            PostLoginDestination.WorkerHome -> {
                viewModel.consumeDestination()
                onNavigateToWorkerHome()
            }
            PostLoginDestination.WorkerOnboarding -> {
                viewModel.consumeDestination()
                onNavigateToWorkerOnboarding()
            }
            PostLoginDestination.ClientHome -> {
                viewModel.consumeDestination()
                onNavigateToClientHome()
            }
            null -> { /* no‑op */ }
        }
    }

    // ── Navigate to Login when check is done and user is not logged in ──
    LaunchedEffect(authState.isCheckingAutoLogin) {
        if (!authState.isCheckingAutoLogin && !authState.isLoggedIn) {
            // Auto-login completed, but no valid session → go to Login
            onNavigateToLogin()
        }
    }

    // ── UI: Simple centered loader ────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // App brand
            Text(
                text = "KaziKonnect",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            // Loading indicator
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            // Optional: show error if auto-login failed
            if (authState.error != null && !authState.isCheckingAutoLogin) {
                Text(
                    text = authState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }
        }
    }
}