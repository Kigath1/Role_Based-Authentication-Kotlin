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
import com.example.Roomdb.data.model.JobStatus
import com.example.Roomdb.data.model.PaymentStatus
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
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize()) {
            // Filter Row with Payment Badge
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.filter == JobFilter.ACTIVE,
                    onClick = { viewModel.setFilter(JobFilter.ACTIVE) },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Active")
                            if (state.waitingForPaymentCount > 0) {
                                Spacer(Modifier.width(4.dp))
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    Text(state.waitingForPaymentCount.toString())
                                }
                            }
                        }
                    },
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
                    message = if (state.filter == JobFilter.ACTIVE) "No active jobs."
                    else "No completed jobs."
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.filteredJobs, key = { it.id }) { job ->
                        var showCounterField by remember(job.id) { mutableStateOf(false) }
                        var counterPrice by remember(job.id) { mutableStateOf("") }

                        // Get payment status for this job
                        val paymentStatus = state.paymentStatusMap[job.id]

                        JobCard(
                            job = job,
                            counterpartyLabel = "Client",
                            isActionInProgress = state.actionInProgressJobId == job.id,
                            paymentStatus = paymentStatus
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                when (job.status) {
                                    JobStatus.PENDING -> {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                    }

                                    JobStatus.ACCEPTED, JobStatus.APPROVED -> {
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            // Payment status indicator
                                            PaymentStatusIndicator(paymentStatus)

                                            when (paymentStatus) {
                                                PaymentStatus.PAID -> {
                                                    Button(
                                                        onClick = { viewModel.startJob(job.id) },
                                                        shape = RoundedCornerShape(10.dp),
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = MaterialTheme.colorScheme.primary,
                                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                                        ),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) { Text("Start Job") }
                                                }
                                                PaymentStatus.NO_PAYMENT -> {
                                                    Text(
                                                        text = "⏳ Waiting for employer to fund escrow...",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.padding(vertical = 4.dp)
                                                    )
                                                }
                                                else -> {
                                                    // Still checking or unknown status
                                                    Text(
                                                        text = "Checking payment status...",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.padding(vertical = 4.dp)
                                                    )
                                                    // Show a disabled Start Job button or just the status
                                                    Button(
                                                        onClick = { /* Disabled */ },
                                                        shape = RoundedCornerShape(10.dp),
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                                        ),
                                                        modifier = Modifier.fillMaxWidth(),
                                                        enabled = false
                                                    ) { Text("Start Job") }
                                                }
                                            }
                                        }
                                    }

                                    JobStatus.IN_PROGRESS -> {
                                        Button(
                                            onClick = { viewModel.completeJob(job.id) },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.secondary,
                                                contentColor = MaterialTheme.colorScheme.onSecondary
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        ) { Text("Mark Complete") }
                                    }

                                    JobStatus.COMPLETED -> {
                                        // Show completed status with no actions
                                        Text(
                                            text = "✅ Job completed",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    }

                                    JobStatus.REJECTED -> {
                                        Text(
                                            text = "❌ Job rejected",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    }

                                    JobStatus.CANCELLED, JobStatus.CLIENT_CANCELLED -> {
                                        Text(
                                            text = "🚫 Job cancelled",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    }

                                    else -> {
                                        // Unknown status - show nothing
                                    }
                                }

                                // Counter offer field (only for PENDING)
                                if (showCounterField && job.status == JobStatus.PENDING) {
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
                                                counterPrice.toDoubleOrNull()?.let {
                                                    viewModel.counterOffer(job.id, it)
                                                }
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

        // Show payment waiting dialog
        state.showPaymentWaitingDialog?.let { jobId ->
            PaymentWaitingDialog(
                jobId = jobId,
                onDismiss = { viewModel.dismissPaymentWaitingDialog() }
            )
        }
    }
}

@Composable
private fun PaymentStatusIndicator(paymentStatus: PaymentStatus?) {
    when (paymentStatus) {
        PaymentStatus.NO_PAYMENT -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "⏳ Waiting for employer to fund escrow...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        PaymentStatus.PAID -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "✅ Payment confirmed! Ready to start.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        PaymentStatus.PENDING, PaymentStatus.WAITING -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "⏳ Payment in progress...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        PaymentStatus.RELEASED -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "💰 Payment released to worker",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        PaymentStatus.FAILED -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "❌ Payment failed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        PaymentStatus.REFUNDED -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "↩️ Payment refunded",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        PaymentStatus.DISPUTED -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "⚠️ Payment in dispute",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        null -> {
            // No payment status available yet
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🔄 Loading payment status...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        else -> {
            // Unknown payment status
        }
    }
}

@Composable
private fun PaymentWaitingDialog(
    jobId: String,
    onDismiss: () -> Unit
) {
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
                    "This may take a few minutes. You can check back later.",
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
            ) {
                Text("Got it")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
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