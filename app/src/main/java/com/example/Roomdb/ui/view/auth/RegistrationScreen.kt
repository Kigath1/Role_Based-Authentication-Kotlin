package com.example.Roomdb.ui.view.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Roomdb.ui.theme.*
import com.example.Roomdb.viewmodel.auth.RegistrationViewModel

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToVerifyEmail: () -> Unit   // ← new callback
) {
    val state by viewModel.state.collectAsState()

    // ─── Observe registration success ─────────────────────────────────────
    LaunchedEffect(state.registrationSuccess) {
        if (state.registrationSuccess) {
            viewModel.consumeRegistrationSuccess()
            onNavigateToVerifyEmail()   // ← navigate to VerifyEmail
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Create an account", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = state.username, onValueChange = viewModel::onUsernameChange,
            label = { Text("Username") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.email, onValueChange = viewModel::onEmailChange,
            label = { Text("Email") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.password, onValueChange = viewModel::onPasswordChange,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.firstName, onValueChange = viewModel::onFirstNameChange,
            label = { Text("First name") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.secondName, onValueChange = viewModel::onSecondNameChange,
            label = { Text("Second name") }, modifier = Modifier.fillMaxWidth()
        )

        Text("I am a:", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Client", "Worker").forEach { role ->
                FilterChip(
                    selected = state.selectedRole == role,
                    onClick = { viewModel.onRoleChange(role) },
                    label = { Text(role) }
                )
            }
        }
        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = viewModel::register,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    if (state.loadingMessage.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            state.loadingMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            } else {
                Text("Register")
            }
        }

        TextButton(onClick = onNavigateToLogin) {
            Text("Already have an account? Log in")
        }
    }
}


//@Composable
//fun RegistrationScreen(
//    viewModel: RegistrationViewModel,
//    onNavigateToLogin: () -> Unit
//) {
//    val state by viewModel.state.collectAsState()
//    var showPass by remember { mutableStateOf(false) }
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
//            Spacer(Modifier.height(52.dp))
//
//            // ── Back + Title ──────────────────────────────────────────────
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                IconButton(onClick = onNavigateToLogin) {
//                    Icon(
//                        Icons.Default.ArrowBack,
//                        contentDescription = "Back",
//                        tint = Color.White
//                    )
//                }
//                Spacer(Modifier.width(4.dp))
//                Column {
//                    Text(
//                        "Create account",
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 26.sp,
//                        color = Color.White
//                    )
//                    Text(
//                        "Join Kenya's worker marketplace",
//                        color = Color.White.copy(alpha = 0.6f),
//                        fontSize = 13.sp
//                    )
//                }
//            }
//
//            Spacer(Modifier.height(28.dp))
//
//            // ── Role selector ─────────────────────────────────────────────
//            Card(
//                shape = RoundedCornerShape(16.dp),
//                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.07f)),
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Row(
//                    modifier = Modifier.padding(6.dp),
//                    horizontalArrangement = Arrangement.spacedBy(6.dp)
//                ) {
//                    listOf("Client", "Worker").forEach { role ->
//                        val selected = state.selectedRole == role
//                        Button(
//                            onClick = { viewModel.onRoleChange(role) },
//                            modifier = Modifier.weight(1f).height(44.dp),
//                            shape = RoundedCornerShape(12.dp),
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = if (selected) KaziGreen else Color.Transparent,
//                                contentColor = if (selected) Color.White else Color.White.copy(alpha = 0.6f)
//                            ),
//                            elevation = ButtonDefaults.buttonElevation(0.dp)
//                        ) {
//                            Text(
//                                role,
//                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
//                                fontSize = 14.sp
//                            )
//                        }
//                    }
//                }
//            }
//
//            Spacer(Modifier.height(20.dp))
//
//            // ── Form card ─────────────────────────────────────────────────
//            Card(
//                shape = RoundedCornerShape(20.dp),
//                colors = CardDefaults.cardColors(containerColor = CardSurface),
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Column(Modifier.padding(24.dp)) {
//
//                    // First + Last name in a row
//                    Row(
//                        horizontalArrangement = Arrangement.spacedBy(12.dp)
//                    ) {
//                        KaziTextField(
//                            value = state.firstName,
//                            onValueChange = viewModel::onFirstNameChange,
//                            label = "First name",
//                            modifier = Modifier.weight(1f)
//                        )
//                        KaziTextField(
//                            value = state.secondName,
//                            onValueChange = viewModel::onSecondNameChange,
//                            label = "Last name",
//                            modifier = Modifier.weight(1f)
//                        )
//                    }
//
//                    Spacer(Modifier.height(14.dp))
//
//                    KaziTextField(
//                        value = state.username,
//                        onValueChange = viewModel::onUsernameChange,
//                        label = "Username"
//                    )
//
//                    Spacer(Modifier.height(14.dp))
//
//                    KaziTextField(
//                        value = state.email,
//                        onValueChange = viewModel::onEmailChange,
//                        label = "Email address",
//                        keyboardType = KeyboardType.Email
//                    )
//
//                    Spacer(Modifier.height(14.dp))
//
//                    KaziTextField(
//                        value = state.password,
//                        onValueChange = viewModel::onPasswordChange,
//                        label = "Password",
//                        keyboardType = KeyboardType.Password,
//                        visualTransformation = if (showPass) VisualTransformation.None
//                        else PasswordVisualTransformation(),
//                        trailingIcon = {
//                            IconButton(onClick = { showPass = !showPass }) {
//                                Icon(
//                                    if (showPass) Icons.Default.VisibilityOff
//                                    else Icons.Default.Visibility,
//                                    contentDescription = null,
//                                    tint = KaziGreen
//                                )
//                            }
//                        }
//                    )
//
//                    // Password hint
//                    Spacer(Modifier.height(6.dp))
//                    Text(
//                        "Min 8 characters, one uppercase, one number",
//                        fontSize = 11.sp,
//                        color = TextGray
//                    )
//
//                    // Error
//                    if (state.error != null) {
//                        Spacer(Modifier.height(10.dp))
//                        Text(state.error!!, color = ErrorRed, fontSize = 13.sp)
//                    }
//
//                    Spacer(Modifier.height(24.dp))
//
//                    KaziPrimaryButton(
//                        text = "Create account",
//                        onClick = viewModel::register,
//                        isLoading = state.isLoading,
//                        enabled = state.email.isNotBlank() && state.password.isNotBlank()
//                                && state.username.isNotBlank() && state.firstName.isNotBlank()
//                                && state.secondName.isNotBlank()
//                    )
//                }
//            }
//
//            Spacer(Modifier.height(20.dp))
//
//            // ── Already have account ──────────────────────────────────────
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    "Already have an account? ",
//                    color = Color.White.copy(alpha = 0.7f),
//                    fontSize = 14.sp
//                )
//                TextButton(onClick = onNavigateToLogin) {
//                    Text("Sign in", color = KaziGold, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
//                }
//            }
//
//            Spacer(Modifier.height(40.dp))
//        }
//    }
//}