package com.example.Roomdb.ui.view.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.example.Roomdb.viewmodel.auth.RegistrationViewModel

@Composable
fun VerifyEmailScreen(
    viewModel: RegistrationViewModel,
    onVerificationSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var token by remember { mutableStateOf("") }

    LaunchedEffect(state.verificationSuccess) {
        if (state.verificationSuccess) {
            viewModel.consumeVerificationSuccess()
            onVerificationSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            elevation = CardDefaults.cardElevation(0.dp),
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = 420.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            shape = androidx.compose.foundation.shape.CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.MailOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    "Verify your email",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "We sent a verification code to ${state.email}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(28.dp))

                // Should CONFIRM token format with backend:
                // - If it's a short numeric code (e.g. 4-6 digits) → keep KaziOtpInputRow below,
                KaziOtpInputRow(
                    value = token,
                    onValueChange = { token = it },
                    length = 6,
                    isError = state.error != null
                )

                if (state.error != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(28.dp))

                KaziPrimaryButton(
                    text = "Verify",
                    onClick = { viewModel.verifyEmail(token) },
                    enabled = !state.isLoading && token.isNotBlank(),
                    isLoading = state.isLoading
                )

                Spacer(Modifier.height(12.dp))

                TextButton(
                    onClick = viewModel::resendVerification,
                    enabled = !state.isLoading
                ) {
                    Text(
                        if (state.resendSuccess) "✓ Email resent!" else "Didn't receive the code? Resend",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (state.resendSuccess)
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}