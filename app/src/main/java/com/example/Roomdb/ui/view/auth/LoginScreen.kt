package com.example.Roomdb.ui.view.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.Roomdb.viewmodel.auth.AuthViewModel
import com.example.Roomdb.viewmodel.auth.PostLoginDestination

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegistration: () -> Unit,
    onNavigateToWorkerHome: () -> Unit,
    onNavigateToWorkerOnboarding: () -> Unit,
    onNavigateToClientHome: () -> Unit,
//    onNavigateToForgotPassword: () -> Unit = {}, // optional — default keeps old call sites compiling
) {
    val state by viewModel.authState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(state.destination) {
        when (state.destination) {
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
            null -> { /* no-op */ }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(64.dp))

            KaziAuthBrandHeader()

            Spacer(Modifier.height(40.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text(
                        "Welcome back",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Please enter your details to continue your journey.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(24.dp))

                    KaziTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        keyboardType = KeyboardType.Email
                    )
                    Spacer(Modifier.height(14.dp))
                    KaziPasswordField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password"
                    )

                    Spacer(Modifier.height(8.dp))
                    TextButton(
                        onClick =  {
                            Toast.makeText(context, "Feature coming soon", Toast.LENGTH_SHORT).show()
                                   },  // onNavigateToForgotPassword,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            "Forgot Password?",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    }

                    if (state.error != null) {
                        Text(
                            text = state.error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    KaziPrimaryButton(
                        text = "Login",
                        onClick = { viewModel.login(email.trim(), password) },
                        enabled = email.isNotBlank() && password.isNotBlank(),
                        isLoading = state.isLoading
                    )

                    Spacer(Modifier.height(20.dp))
                    KaziDividerWithText()
                    Spacer(Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        KaziSocialButton(
                            text = "Google",
                            icon = { /* TODO: swap for Google "G" icon asset */ },
                            onClick = { /* TODO: wire Google sign-in */ },
                            modifier = Modifier.weight(1f)
                        )
                        KaziSocialButton(
                            text = "Apple",
                            icon = { /* TODO: swap for Apple icon asset */ },
                            onClick = { /* TODO: wire Apple sign-in */ },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Don't have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onNavigateToRegistration) {
                    Text(
                        "Join Konnect",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}