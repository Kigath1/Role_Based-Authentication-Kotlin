package com.example.Roomdb.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.Roomdb.data.model.UserProfile
import com.example.Roomdb.viewmodel.AuthViewModel

@Composable
fun WorkerHomeScreen(
    viewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    var profile by remember { mutableStateOf<UserProfile?>(null) }

    LaunchedEffect(Unit) {
        profile = viewModel.getCurrentUser()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // Header (replaces TopAppBar — no experimental API needed)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Worker Dashboard",
                style = MaterialTheme.typography.titleLarge
            )
            TextButton(onClick = { viewModel.logout { onLogout() } }) {
                Text("Log out")
            }
        }

        HorizontalDivider()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            profile?.let { user ->
                Text("Welcome, ${user.name}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text("Role: ${user.role}")
                Text("Email: ${user.email}")
            } ?: CircularProgressIndicator()
        }
    }
}