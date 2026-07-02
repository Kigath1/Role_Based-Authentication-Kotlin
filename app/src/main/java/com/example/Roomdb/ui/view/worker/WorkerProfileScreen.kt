package com.example.Roomdb.ui.view.worker

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
import com.example.Roomdb.viewmodel.worker.WorkerProfileViewModel

@Composable
fun WorkerProfileScreen(
    viewModel: WorkerProfileViewModel,
    currentUserId: String,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotBlank()) viewModel.loadProfile(currentUserId)
    }
    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it); viewModel.clearError() }
    }
    LaunchedEffect(state.saveSuccessMessage) {
        state.saveSuccessMessage?.let { snackbarHostState.showSnackbar(it); viewModel.consumeSaveSuccess() }
    }

    if (state.showReapprovalDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissReapprovalWarning,
            title = { Text("Heads up") },
            text = {
                Text(
                    "Editing your profile will require admin re-approval. " +
                            "You'll be temporarily hidden from the marketplace until then. Continue?"
                )
            },
            confirmButton = { TextButton(onClick = viewModel::confirmEditAfterWarning) { Text("Continue") } },
            dismissButton = { TextButton(onClick = viewModel::dismissReapprovalWarning) { Text("Cancel") } }
        )
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
                Text("My Profile", style = MaterialTheme.typography.headlineSmall)
                StatusBadge(status = state.status)

                LockedField("Email", state.email)
                EditableOrLockedField("Full Name", state.fullName, state.isEditing, viewModel::onFullNameChange)
                EditableOrLockedField("Phone Number", state.phoneNumber, state.isEditing, viewModel::onPhoneNumberChange)
                EditableOrLockedField("Category", state.category, state.isEditing, viewModel::onCategoryChange)
                EditableOrLockedField("Location", state.location, state.isEditing, viewModel::onLocationChange)
                EditableOrLockedField("Experience (years)", state.experienceYears, state.isEditing, viewModel::onExperienceChange)
                EditableOrLockedField("Hourly Rate (KES)", state.hourlyRate, state.isEditing, viewModel::onHourlyRateChange)
                EditableOrLockedField("Skills (comma separated)", state.skillsRaw, state.isEditing, viewModel::onSkillsChange)
                EditableOrLockedField("Preferred Locations (comma separated)", state.preferredLocationsRaw, state.isEditing, viewModel::onPreferredLocationsChange)
                EditableOrLockedField("Bio", state.bio, state.isEditing, viewModel::onBioChange)

                Text("Availability", style = MaterialTheme.typography.labelLarge)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = state.availabilityWeekdays, onCheckedChange = viewModel::onAvailabilityWeekdaysChange, enabled = state.isEditing)
                    Text("Weekdays")
                    Spacer(Modifier.width(12.dp))
                    Checkbox(checked = state.availabilityWeekends, onCheckedChange = viewModel::onAvailabilityWeekendsChange, enabled = state.isEditing)
                    Text("Weekends")
                    Spacer(Modifier.width(12.dp))
                    Checkbox(checked = state.availabilityEvenings, onCheckedChange = viewModel::onAvailabilityEveningsChange, enabled = state.isEditing)
                    Text("Evenings")
                }

                Spacer(Modifier.height(8.dp))

                if (state.isEditing) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = viewModel::cancelEditing, enabled = !state.isSaving, modifier = Modifier.weight(1f)) {
                            Text("Cancel")
                        }
                        Button(onClick = viewModel::save, enabled = !state.isSaving, modifier = Modifier.weight(1f)) {
                            if (state.isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            else Text("Save")
                        }
                    }
                } else {
                    Button(onClick = viewModel::requestEdit, modifier = Modifier.fillMaxWidth()) {
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
private fun StatusBadge(status: String) {
    val color = when (status) {
        "APPROVED" -> MaterialTheme.colorScheme.primary
        "REJECTED" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.secondary // DRAFT / PENDING
    }
    AssistChip(
        onClick = {},
        label = { Text(status.ifBlank { "DRAFT" }) },
        colors = AssistChipDefaults.assistChipColors(labelColor = color)
    )
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