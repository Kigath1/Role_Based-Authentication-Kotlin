package com.example.Roomdb.ui.view.employer


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Roomdb.ui.theme.*
import com.example.Roomdb.ui.view.auth.KaziPrimaryButton
import com.example.Roomdb.ui.view.auth.KaziTextField
import com.example.Roomdb.viewmodel.employer.ClientProfileSetupViewModel

@Composable
fun ClientProfileSetupScreen(
    viewModel: ClientProfileSetupViewModel,
    email: String
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Tell us about you", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = state.fullName, onValueChange = viewModel::onFullNameChange,
            label = { Text("Full name") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.phoneNumber, onValueChange = viewModel::onPhoneNumberChange,
            label = { Text("Phone number") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.location, onValueChange = viewModel::onLocationChange,
            label = { Text("Location") }, modifier = Modifier.fillMaxWidth()
        )

        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = { viewModel.createProfile(email) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Continue")
            }
        }
    }
}


///**
// * Client profile setup — shown once, after email verification.
// * Success is observed in AppNavHost via [ClientProfileSetupViewModel.state.success].
// *
// * @param email   Comes from AuthViewModel.getCurrentUserEmail() after auto-login.
// *                Passed to the use case as the ?email= query param.
// */
//@Composable
//fun ClientProfileSetupScreen(
//    viewModel: ClientProfileSetupViewModel,
//    email: String
//) {
//    val state by viewModel.state.collectAsState()
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
//                .verticalScroll(rememberScrollState())
//                .padding(horizontal = 28.dp)
//        ) {
//            Spacer(Modifier.height(56.dp))
//
//            // ── Header ────────────────────────────────────────────────────
//            Text(
//                "Set up your profile",
//                fontWeight = FontWeight.Bold,
//                fontSize = 28.sp,
//                color = Color.White
//            )
//            Spacer(Modifier.height(6.dp))
//            Text(
//                "Tell workers a bit about you so they know who they're working with.",
//                color = Color.White.copy(alpha = 0.65f),
//                fontSize = 14.sp,
//                lineHeight = 22.sp
//            )
//
//            Spacer(Modifier.height(32.dp))
//
//            // ── Progress indicator ────────────────────────────────────────
//            LinearProgressIndicator(
//                progress = { 1f },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(4.dp),
//                color = KaziGold,
//                trackColor = Color.White.copy(alpha = 0.15f)
//            )
//
//            Spacer(Modifier.height(28.dp))
//
//            // ── Form card ─────────────────────────────────────────────────
//            Card(
//                shape = RoundedCornerShape(20.dp),
//                colors = CardDefaults.cardColors(containerColor = CardSurface),
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Column(Modifier.padding(24.dp)) {
//
//                    Text(
//                        "Your details",
//                        fontWeight = FontWeight.SemiBold,
//                        fontSize = 17.sp,
//                        color = SurfaceDark
//                    )
//
//                    Spacer(Modifier.height(20.dp))
//
//                    KaziTextField(
//                        value = state.fullName,
//                        onValueChange = viewModel::onFullNameChange,
//                        label = "Full name"
//                    )
//
//                    Spacer(Modifier.height(14.dp))
//
//                    KaziTextField(
//                        value = state.phoneNumber,
//                        onValueChange = viewModel::onPhoneNumberChange,
//                        label = "Phone number (254XXXXXXXXX)",
//                        keyboardType = KeyboardType.Phone
//                    )
//
//                    Spacer(Modifier.height(14.dp))
//
//                    KaziTextField(
//                        value = state.location,
//                        onValueChange = viewModel::onLocationChange,
//                        label = "Location (e.g. Nairobi)"
//                    )
//
//                    // Error
//                    if (state.error != null) {
//                        Spacer(Modifier.height(12.dp))
//                        Surface(
//                            color = ErrorRed.copy(alpha = 0.08f),
//                            shape = RoundedCornerShape(10.dp)
//                        ) {
//                            Text(
//                                text = state.error!!,
//                                color = ErrorRed,
//                                fontSize = 13.sp,
//                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
//                            )
//                        }
//                    }
//
//                    Spacer(Modifier.height(24.dp))
//
//                    KaziPrimaryButton(
//                        text = "Save and continue",
//                        onClick = { viewModel.createProfile(email) },
//                        isLoading = state.isLoading,
//                        enabled = state.fullName.isNotBlank()
//                                && state.phoneNumber.isNotBlank()
//                                && state.location.isNotBlank()
//                    )
//                }
//            }
//
//            Spacer(Modifier.height(40.dp))
//        }
//    }
//}