package com.example.Roomdb.ui.view.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.Roomdb.viewmodel.auth.RegistrationViewModel

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToVerifyEmail: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.registrationSuccess) {
        if (state.registrationSuccess) {
            viewModel.consumeRegistrationSuccess()
            onNavigateToVerifyEmail()
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
                .padding(horizontal = 24.dp, vertical = 32.dp),
        ) {
            Text(
                "Join Konnect",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Create an account to find work or hire trusted professionals.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(28.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "I am a:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        RoleOptionCard(
                            label = "Client",
                            subtitle = "I want to hire",
                            icon = Icons.Default.Work,
                            selected = state.selectedRole == "Client",
                            onClick = { viewModel.onRoleChange("Client") },
                            modifier = Modifier.weight(1f)
                        )
                        RoleOptionCard(
                            label = "Worker",
                            subtitle = "I want to work",
                            icon = Icons.Default.Handyman,
                            selected = state.selectedRole == "Worker",
                            onClick = { viewModel.onRoleChange("Worker") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        KaziTextField(
                            value = state.firstName,
                            onValueChange = viewModel::onFirstNameChange,
                            label = "First name",
                            modifier = Modifier.weight(1f)
                        )
                        KaziTextField(
                            value = state.secondName,
                            onValueChange = viewModel::onSecondNameChange,
                            label = "Second name",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    KaziTextField(
                        value = state.username,
                        onValueChange = viewModel::onUsernameChange,
                        label = "Username"
                    )

                    KaziTextField(
                        value = state.email,
                        onValueChange = viewModel::onEmailChange,
                        label = "Email Address",
                        keyboardType = KeyboardType.Email
                    )

                    KaziPasswordField(
                        value = state.password,
                        onValueChange = viewModel::onPasswordChange,
                        label = "Password"
                    )

                    if (state.error != null) {
                        Text(
                            state.error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    KaziPrimaryButton(
                        text = if (state.isLoading && state.loadingMessage.isNotEmpty())
                            state.loadingMessage else "Create Account",
                        onClick = viewModel::register,
                        enabled = !state.isLoading,
                        isLoading = state.isLoading
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        "Log in",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleOptionCard(
    label: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected)
        MaterialTheme.colorScheme.secondaryContainer
    else
        MaterialTheme.colorScheme.surfaceContainer

    val contentColor = if (selected)
        MaterialTheme.colorScheme.onSecondaryContainer
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = if (selected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary) else null,
        modifier = modifier
    ) {
        Column(
            Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = contentColor)
            Spacer(Modifier.height(6.dp))
            Text(label, style = MaterialTheme.typography.titleSmall, color = contentColor, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = contentColor)
        }
    }
}