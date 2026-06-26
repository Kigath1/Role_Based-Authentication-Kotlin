package com.example.Roomdb.ui.view.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Roomdb.ui.theme.*
import com.example.Roomdb.viewmodel.auth.RegistrationViewModel

@Composable
fun VerifyEmailScreen(
    viewModel: RegistrationViewModel,
    onGoToLogin: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Verify your email",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "We've sent a verification link to your email address. " +
                    "Click the link in the email, then come back here to log in.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))

        // Resend button
        OutlinedButton(
            onClick = viewModel::resendVerification,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (state.resendSuccess) "✓ Email resent!" else "Resend verification email")
        }

        Spacer(Modifier.height(12.dp))

        // After verifying in email, user taps this
        Button(
            onClick = onGoToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("I've verified my email — Log in")
        }

        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall)
        }
    }
}


///**
// * Email verification screen.
// *
// * The user receives a token via email after registration and types it here.
// * On success, AppNavHost observes [RegistrationViewModel.state.verificationSuccess]
// * and handles navigation — this screen only drives the ViewModel.
// */
//@Composable
//fun VerifyEmailScreen(
//    viewModel: RegistrationViewModel
//) {
//    val state by viewModel.state.collectAsState()
//
//    // We show the email that was registered, pulled from pending credentials
//    val (pendingEmail, _, _) = remember { viewModel.getPendingCredentials() }
//    val displayEmail = pendingEmail.ifBlank { state.email }
//
//    var token by remember { mutableStateOf("") }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                Brush.verticalGradient(listOf(SurfaceDark, Color(0xFF1B3A26)))
//            )
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(horizontal = 28.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Spacer(Modifier.height(80.dp))
//
//            // ── Email icon placeholder ────────────────────────────────────
//            Surface(
//                modifier = Modifier.size(80.dp),
//                shape = RoundedCornerShape(24.dp),
//                color = KaziGreen.copy(alpha = 0.15f)
//            ) {
//                Box(contentAlignment = Alignment.Center) {
//                    Text("✉️", fontSize = 36.sp)
//                }
//            }
//
//            Spacer(Modifier.height(28.dp))
//
//            Text(
//                "Check your email",
//                fontWeight = FontWeight.Bold,
//                fontSize = 26.sp,
//                color = Color.White,
//                textAlign = TextAlign.Center
//            )
//
//            Spacer(Modifier.height(10.dp))
//
//            Text(
//                text = "We sent a verification code to\n$displayEmail",
//                color = Color.White.copy(alpha = 0.65f),
//                fontSize = 14.sp,
//                textAlign = TextAlign.Center,
//                lineHeight = 22.sp
//            )
//
//            Spacer(Modifier.height(40.dp))
//
//            // ── Token input card ──────────────────────────────────────────
//            Card(
//                shape = RoundedCornerShape(20.dp),
//                colors = CardDefaults.cardColors(containerColor = CardSurface),
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Column(Modifier.padding(24.dp)) {
//                    Text(
//                        "Enter verification code",
//                        fontWeight = FontWeight.SemiBold,
//                        fontSize = 16.sp,
//                        color = SurfaceDark
//                    )
//                    Spacer(Modifier.height(4.dp))
//                    Text(
//                        "Paste or type the code from your email",
//                        fontSize = 13.sp,
//                        color = TextGray
//                    )
//                    Spacer(Modifier.height(20.dp))
//
//                    // Single token field — the API takes the full token as a query param
//                    KaziTextField(
//                        value = token,
//                        onValueChange = { token = it },
//                        label = "Verification code",
//                        keyboardType = KeyboardType.Text
//                    )
//
//                    if (state.error != null) {
//                        Spacer(Modifier.height(10.dp))
//                        Text(state.error!!, color = ErrorRed, fontSize = 13.sp)
//                    }
//
//                    if (state.resendSuccess) {
//                        Spacer(Modifier.height(10.dp))
//                        Text(
//                            "Code resent. Check your inbox.",
//                            color = KaziGreen,
//                            fontSize = 13.sp
//                        )
//                    }
//
//                    Spacer(Modifier.height(24.dp))
//
//                    KaziPrimaryButton(
//                        text = "Verify email",
//                        onClick = { viewModel.verifyEmail(token.trim()) },
//                        isLoading = state.isLoading,
//                        enabled = token.isNotBlank()
//                    )
//
//                    Spacer(Modifier.height(14.dp))
//
//                    // Resend link
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.Center,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            "Didn't receive it? ",
//                            color = TextGray,
//                            fontSize = 13.sp
//                        )
//                        TextButton(
//                            onClick = viewModel::resendVerification,
//                            contentPadding = PaddingValues(horizontal = 4.dp)
//                        ) {
//                            Text(
//                                "Resend code",
//                                color = KaziGreen,
//                                fontWeight = FontWeight.SemiBold,
//                                fontSize = 13.sp
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}