package com.example.Roomdb.ui.view.worker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Roomdb.ui.view.common.cards.JobCard
import com.example.Roomdb.viewmodel.employer.JobFilter
import com.example.Roomdb.viewmodel.worker.WorkerJobsViewModel

@Composable
fun WorkerJobsTab(
    viewModel: WorkerJobsViewModel,
    currentUserId: String
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotBlank()) viewModel.loadJobs(currentUserId)
    }
    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it); viewModel.clearError() }
    }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.filter == JobFilter.ACTIVE,
                    onClick = { viewModel.setFilter(JobFilter.ACTIVE) },
                    label = { Text("Active") },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                FilterChip(
                    selected = state.filter == JobFilter.COMPLETED,
                    onClick = { viewModel.setFilter(JobFilter.COMPLETED) },
                    label = { Text("Completed") },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
                state.filteredJobs.isEmpty() -> EmptyJobsState(
                    message = if (state.filter == JobFilter.ACTIVE) "No incoming requests yet."
                    else "No completed jobs yet."
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.filteredJobs, key = { it.id }) { job ->
                        var showCounterField by remember(job.id) { mutableStateOf(false) }
                        var counterPrice by remember(job.id) { mutableStateOf("") }

                        JobCard(
                            job = job,
                            counterpartyLabel = "Client",
                            isActionInProgress = state.actionInProgressJobId == job.id
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                when (job.status.name) {
                                    "PENDING" -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { viewModel.acceptJob(job.id) },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            )
                                        ) { Text("Accept") }
                                        OutlinedButton(
                                            onClick = { viewModel.rejectJob(job.id) },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = MaterialTheme.colorScheme.error
                                            )
                                        ) { Text("Reject") }
                                        OutlinedButton(
                                            onClick = { showCounterField = !showCounterField },
                                            shape = RoundedCornerShape(10.dp)
                                        ) { Text("Counter") }
                                    }
                                    "ACCEPTED" -> Button(
                                        onClick = { viewModel.startJob(job.id) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    ) { Text("Start Job") }
                                    "IN_PROGRESS" -> Button(
                                        onClick = { viewModel.completeJob(job.id) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondary,
                                            contentColor = MaterialTheme.colorScheme.onSecondary
                                        )
                                    ) { Text("Mark Complete") }
                                    else -> {}
                                }

                                if (showCounterField) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = counterPrice,
                                            onValueChange = { counterPrice = it },
                                            label = { Text("Your price (KSh)") },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Button(
                                            onClick = {
                                                counterPrice.toDoubleOrNull()?.let { viewModel.counterOffer(job.id, it) }
                                                showCounterField = false
                                            },
                                            enabled = counterPrice.toDoubleOrNull() != null,
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            )
                                        ) { Text("Send") }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun EmptyJobsState(message: String) {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Outlined.SearchOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}