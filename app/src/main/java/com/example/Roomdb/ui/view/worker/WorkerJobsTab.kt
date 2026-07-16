package com.example.Roomdb.ui.view.worker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Roomdb.data.model.JobStatus
import com.example.Roomdb.ui.view.common.cards.JobCard
import com.example.Roomdb.viewmodel.employer.JobFilter
import com.example.Roomdb.viewmodel.worker.WorkerJobsUiState
import com.example.Roomdb.viewmodel.worker.WorkerJobsViewModel

@Composable
fun WorkerJobsTab(
    viewModel: WorkerJobsViewModel,
    currentUserId: String,
    onGoToWallet: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Load jobs when userId changes
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotBlank()) {
            viewModel.loadJobs(currentUserId)
        }
    }

    // Show errors
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column {
                // Header
                JobsHeader()

                // Filter Row
                FilterRow(state, viewModel)

                // Content
                when {
                    state.isLoading -> LoadingState()
                    state.filteredJobs.isEmpty() -> EmptyState(state.filter)
                    else -> JobList(
                        state = state,
                        viewModel = viewModel,
                        onGoToWallet = onGoToWallet
                    )
                }
            }

            // Payment waiting dialog
            state.showPaymentWaitingDialog?.let {
                PaymentWaitingDialog(
                    onDismiss = { viewModel.dismissPaymentWaitingDialog() }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Header Section
// ─────────────────────────────────────────────────────────────
@Composable
private fun JobsHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            "My Jobs",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Manage your active and completed service requests",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Filter Row
// ─────────────────────────────────────────────────────────────
@Composable
private fun FilterRow(
    state: WorkerJobsUiState,
    viewModel: WorkerJobsViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        FilterTab(
            label = "Active",
            selected = state.filter == JobFilter.ACTIVE,
            onClick = { viewModel.setFilter(JobFilter.ACTIVE) },
            badgeCount = state.waitingForPaymentCount
        )
        FilterTab(
            label = "Completed",
            selected = state.filter == JobFilter.COMPLETED,
            onClick = { viewModel.setFilter(JobFilter.COMPLETED) }
        )
    }
}

@Composable
private fun FilterTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    badgeCount: Int = 0
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable(onClick = onClick)
            )
            if (badgeCount > 0) {
                Spacer(Modifier.width(4.dp))
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(badgeCount.toString())
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        if (selected) {
            Box(
                modifier = Modifier
                    .height(2.dp)
                    .width(28.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Job List
// ─────────────────────────────────────────────────────────────
@Composable
private fun JobList(
    state: WorkerJobsUiState,
    viewModel: WorkerJobsViewModel,
    onGoToWallet: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.filteredJobs, key = { it.id }) { job ->
            val paymentStatus = state.paymentStatusMap[job.id]

            JobCard(
                job = job,
                isActionInProgress = state.actionInProgressJobId == job.id,
                paymentStatus = paymentStatus,
                onAccept = { viewModel.acceptJob(job.id) },
                onReject = { viewModel.rejectJob(job.id) },
                onCounter = { price -> viewModel.counterOffer(job.id, price) },
                onStart = { viewModel.startJob(job.id) },
                onComplete = { viewModel.completeJob(job.id) },
                onWithdraw = onGoToWallet,
                onViewReceipt = { /* Navigate to receipt */ },
                onViewDetails = { /* Navigate to job details */ }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// State Components
// ─────────────────────────────────────────────────────────────
@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun EmptyState(filter: JobFilter) {
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
                text = when (filter) {
                    JobFilter.ACTIVE -> "No active jobs available."
                    JobFilter.COMPLETED -> "No completed jobs yet."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Payment Waiting Dialog
// ─────────────────────────────────────────────────────────────
@Composable
private fun PaymentWaitingDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Waiting for Payment")
            }
        },
        text = {
            Column {
                Text("Job accepted! The employer has been notified to fund the escrow.")
                Spacer(Modifier.height(8.dp))
                Text(
                    "The job will automatically become available to start once payment is confirmed.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "You will be notified when the payment is confirmed.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) { Text("Got it") }
        },
        shape = RoundedCornerShape(20.dp)
    )
}