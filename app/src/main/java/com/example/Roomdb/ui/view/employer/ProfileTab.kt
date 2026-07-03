package com.example.Roomdb.ui.view.employer


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.Roomdb.viewmodel.employer.ClientProfileViewModel

@Composable
fun ProfileTab(
    viewModel: ClientProfileViewModel,
    currentUserId: String,
    currentUserEmail: String,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotBlank()) viewModel.loadProfile(currentUserId, currentUserEmail)
    }
    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it); viewModel.clearError() }
    }
    LaunchedEffect(state.saveSuccessMessage) {
        state.saveSuccessMessage?.let { snackbarHostState.showSnackbar(it); viewModel.consumeSaveSuccess() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    if (!state.profileExists) "Complete Your Profile" else "My Profile",
                    style = MaterialTheme.typography.headlineSmall
                )

                LockedField("Email", state.email)
                EditableOrLockedField("Full Name", state.fullName, state.isEditing, viewModel::onFullNameChange)
                EditableOrLockedField("Phone Number", state.phoneNumber, state.isEditing, viewModel::onPhoneNumberChange)
                EditableOrLockedField(
                    "Location", state.location,
                    editable = state.isEditing && !state.profileExists,
                    onValueChange = viewModel::onLocationChange
                )

                Spacer(Modifier.height(8.dp))

                if (state.isEditing) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (state.profileExists) {
                            OutlinedButton(
                                onClick = viewModel::cancelEditing,
                                enabled = !state.isSaving,
                                modifier = Modifier.weight(1f)
                            ) { Text("Cancel") }
                        }
                        Button(
                            onClick = viewModel::save,
                            enabled = !state.isSaving,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (state.isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            else Text(if (state.profileExists) "Save" else "Create Profile")
                        }
                    }
                } else {
                    Button(onClick = viewModel::startEditing, modifier = Modifier.fillMaxWidth()) {
                        Text("Edit Profile")
                    }
                }

                Spacer(Modifier.weight(1f))
                OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text("Log out") }
            }
        }

        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun LockedField(label: String, value: String) {
    OutlinedTextField(
        value = value, onValueChange = {}, label = { Text(label) }, enabled = false,
        trailingIcon = { Icon(Icons.Outlined.Lock, contentDescription = "Locked") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun EditableOrLockedField(
    label: String, value: String, editable: Boolean, onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, label = { Text(label) }, enabled = editable,
        trailingIcon = { if (!editable) Icon(Icons.Outlined.Lock, contentDescription = "Locked") },
        modifier = Modifier.fillMaxWidth()
    )
}


