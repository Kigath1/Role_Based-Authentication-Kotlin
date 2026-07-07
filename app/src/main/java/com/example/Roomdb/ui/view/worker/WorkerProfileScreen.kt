package com.example.Roomdb.ui.view.worker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
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

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileHeroHeader(fullName = state.fullName, status = state.status)

                SectionCard(title = "Contact & Account") {
                    LockedField("Email", state.email)
                }

                SectionCard(title = "Professional Details") {
                    EditableOrLockedField("Full Name", state.fullName, state.isEditing, viewModel::onFullNameChange)
                    EditableOrLockedField("Phone Number", state.phoneNumber, state.isEditing, viewModel::onPhoneNumberChange)
                    EditableOrLockedField("Category", state.category, state.isEditing, viewModel::onCategoryChange)
                    EditableOrLockedField("Location", state.location, state.isEditing, viewModel::onLocationChange)
                    EditableOrLockedField("Experience (years)", state.experienceYears, state.isEditing, viewModel::onExperienceChange)
                    EditableOrLockedField("Hourly Rate (KES)", state.hourlyRate, state.isEditing, viewModel::onHourlyRateChange)
                    EditableOrLockedField("Skills (comma separated)", state.skillsRaw, state.isEditing, viewModel::onSkillsChange)
                    EditableOrLockedField("Preferred Locations (comma separated)", state.preferredLocationsRaw, state.isEditing, viewModel::onPreferredLocationsChange)
                    EditableOrLockedField("Bio", state.bio, state.isEditing, viewModel::onBioChange)
                }

                SectionCard(title = "Availability") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = state.availabilityWeekdays,
                            onCheckedChange = viewModel::onAvailabilityWeekdaysChange,
                            enabled = state.isEditing,
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text("Weekdays", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.width(12.dp))
                        Checkbox(
                            checked = state.availabilityWeekends,
                            onCheckedChange = viewModel::onAvailabilityWeekendsChange,
                            enabled = state.isEditing,
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text("Weekends", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.width(12.dp))
                        Checkbox(
                            checked = state.availabilityEvenings,
                            onCheckedChange = viewModel::onAvailabilityEveningsChange,
                            enabled = state.isEditing,
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text("Evenings", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                if (state.isEditing) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = viewModel::cancelEditing,
                            enabled = !state.isSaving,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("Cancel") }
                        Button(
                            onClick = viewModel::save,
                            enabled = !state.isSaving,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            if (state.isSaving) CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            else Text("Save")
                        }
                    }
                } else {
                    Button(
                        onClick = viewModel::requestEdit,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) { Text("Edit Profile") }
                }

                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Log out") }

                Spacer(Modifier.height(8.dp))
            }
        }

        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

// ─────────────────────────────────────────────────────────────
// Hero header — avatar + name + status chip, mirrors the mockup's
// worker-profile hero block (no portfolio/reviews here, since this
// is the worker's own editable profile, not the public-facing one).
// ─────────────────────────────────────────────────────────────

@Composable
private fun ProfileHeroHeader(fullName: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                fullName.take(2).uppercase().ifBlank { "?" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                fullName.ifBlank { "Your Name" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            StatusBadge(status = status)
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (containerColor, contentColor, showVerifiedIcon) = when (status) {
        "APPROVED" -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            true
        )
        "REJECTED" -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            false
        )
        else -> Triple( // DRAFT / PENDING
            MaterialTheme.colorScheme.surfaceContainerHigh,
            MaterialTheme.colorScheme.onSurfaceVariant,
            false
        )
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(containerColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showVerifiedIcon) {
            Icon(
                Icons.Outlined.Verified,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
        }
        Text(
            status.ifBlank { "DRAFT" },
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.Bold
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Section card wrapper — groups related fields visually, matching
// the mockup's stat-card/rounded-container treatment.
// ─────────────────────────────────────────────────────────────

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        content()
    }
}

@Composable
private fun LockedField(label: String, value: String) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        enabled = false,
        trailingIcon = { Icon(Icons.Outlined.Lock, contentDescription = "Locked") },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun EditableOrLockedField(
    label: String, value: String, editable: Boolean, onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = editable,
        trailingIcon = { if (!editable) Icon(Icons.Outlined.Lock, contentDescription = "Locked") },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )
}