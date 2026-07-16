package com.example.Roomdb.ui.view.common.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Roomdb.data.model.Job
import com.example.Roomdb.data.model.JobStatus
import com.example.Roomdb.data.model.PaymentStatus

@Composable
fun JobCard(
    job: Job,
    isActionInProgress: Boolean,
    paymentStatus: PaymentStatus? = null,
    onAccept: () -> Unit = {},
    onReject: () -> Unit = {},
    onCounter: (Double) -> Unit = {},
    onStart: () -> Unit = {},
    onComplete: () -> Unit = {},
    onWithdraw: () -> Unit = {},
    onViewReceipt: () -> Unit = {},
    onViewDetails: () -> Unit = {}
) {
    val accentColor = statusAccentColor(job.status)
    val isFinished = job.status in setOf(JobStatus.COMPLETED, JobStatus.APPROVED)
    val isPending = job.status == JobStatus.PENDING
    val isAccepted = job.status == JobStatus.ACCEPTED
    val isInProgress = job.status == JobStatus.IN_PROGRESS
    val isCompleted = job.status == JobStatus.COMPLETED
    val isApproved = job.status == JobStatus.APPROVED
    val isRejected = job.status == JobStatus.REJECTED
    val isCancelled = job.status in setOf(JobStatus.CANCELLED, JobStatus.CLIENT_CANCELLED)

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isFinished) {
            MaterialTheme.colorScheme.surfaceContainerLow
        } else {
            MaterialTheme.colorScheme.surfaceContainerLowest
        },
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.fillMaxWidth()) {
            // Left accent strip
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accentColor)
            )

            Column(
                Modifier
                    .padding(16.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // ── Header Row: Description + Status ──
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        job.description,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isFinished) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    StatusChip(job.status, paymentStatus)
                }

                // ── Client Name ──
                job.client?.fullName?.let { clientName ->
                    InfoRow(
                        icon = Icons.Outlined.Person,
                        text = "Client: $clientName"
                    )
                }

                // ── Location ──
                job.location?.let { location ->
                    InfoRow(
                        icon = Icons.Outlined.LocationOn,
                        text = location
                    )
                }

                // ── Scheduled Date ──
                job.scheduledDate?.let { date ->
                    InfoRow(
                        icon = Icons.Outlined.CalendarToday,
                        text = date
                    )
                }

                // ── Price ──
                val displayPrice = job.negotiatedPrice ?: job.jobPrice ?: job.totalCost
                displayPrice?.let {
                    Text(
                        "💰 KSh ${formatPrice(it)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                // ── Payment Amount (for completed/approved jobs) ──
                if (isCompleted || isApproved) {
                    job.paymentAmount?.let { amount ->
                        InfoRow(
                            icon = Icons.Outlined.Payments,
                            text = "Payment: KSh ${formatPrice(amount)}"
                        )
                    }

                    job.platformFee?.let { fee ->
                        InfoRow(
                            icon = Icons.Outlined.Info,
                            text = "Platform Fee: KSh ${formatPrice(fee)}",
                            textColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    job.mpesaReceiptNumber?.let { receipt ->
                        InfoRow(
                            icon = Icons.Outlined.Receipt,
                            text = "Receipt: $receipt"
                        )
                    }
                }

                // ── Escrow Amount (for accepted jobs) ──
                if (isAccepted && job.paymentAmount != null) {
                    job.paymentAmount?.let { amount ->
                        if (amount > 0) {
                            InfoRow(
                                icon = Icons.Outlined.Lock,
                                text = "Escrow: KSh ${formatPrice(amount)}",
                                textColor = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // ── Escrow Message ──
                job.escrowMessage?.let { message ->
                    Text(
                        message,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // ── Payment Status Indicator ──
                if (isAccepted || isApproved) {
                    PaymentStatusIndicator(
                        paymentStatus = paymentStatus,
                        job = job
                    )
                }

                // ── Counter Offer Pending ──
                if (job.negotiatedPrice != null && isPending) {
                    Text(
                        "📝 Counter-offer pending",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    )
                }

                // ── Rejection / Cancellation Reasons ──
                job.rejectionReason?.let {
                    Text(
                        "❌ Rejected: $it",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                job.cancellationReason?.let {
                    Text(
                        "🚫 Cancelled: $it",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // ── Action Buttons ──
                if (isActionInProgress) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                } else {
                    JobActionButtons(
                        job = job,
                        paymentStatus = paymentStatus,
                        onAccept = onAccept,
                        onReject = onReject,
                        onCounter = onCounter,
                        onStart = onStart,
                        onComplete = onComplete,
                        onWithdraw = onWithdraw,
                        onViewReceipt = onViewReceipt,
                        onViewDetails = onViewDetails
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Info Row
// ─────────────────────────────────────────────────────────────
@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    textColor: androidx.compose.ui.graphics.Color? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = textColor ?: MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Status Chip
// ─────────────────────────────────────────────────────────────
@Composable
private fun StatusChip(
    status: JobStatus,
    paymentStatus: PaymentStatus? = null
) {
    val (containerColor, contentColor, label) = when (status) {
        JobStatus.PENDING ->
            Triple(
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.onSecondaryContainer,
                "PENDING"
            )
        JobStatus.ACCEPTED -> {
            when (paymentStatus) {
                PaymentStatus.PAID ->
                    Triple(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.primary,
                        "PAYMENT CONFIRMED"
                    )
                PaymentStatus.NO_PAYMENT, PaymentStatus.PENDING ->
                    Triple(
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.error,
                        "AWAITING PAYMENT"
                    )
                else ->
                    Triple(
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.onSecondaryContainer,
                        "ACCEPTED"
                    )
            }
        }
        JobStatus.APPROVED ->
            Triple(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                MaterialTheme.colorScheme.primary,
                "APPROVED"
            )
        JobStatus.IN_PROGRESS ->
            Triple(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                MaterialTheme.colorScheme.primary,
                "IN PROGRESS"
            )
        JobStatus.COMPLETED ->
            Triple(
                MaterialTheme.colorScheme.tertiaryContainer,
                MaterialTheme.colorScheme.onTertiaryContainer,
                "COMPLETED"
            )
        JobStatus.REJECTED ->
            Triple(
                MaterialTheme.colorScheme.errorContainer,
                MaterialTheme.colorScheme.onErrorContainer,
                "REJECTED"
            )
        JobStatus.CANCELLED, JobStatus.CLIENT_CANCELLED ->
            Triple(
                MaterialTheme.colorScheme.errorContainer,
                MaterialTheme.colorScheme.onErrorContainer,
                "CANCELLED"
            )
        else ->
            Triple(
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.onSecondaryContainer,
                status.name
            )
    }

    AssistChip(
        onClick = {},
        label = {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = containerColor,
            labelColor = contentColor
        ),
        border = null
    )
}

// ─────────────────────────────────────────────────────────────
// Payment Status Indicator
// ─────────────────────────────────────────────────────────────
@Composable
private fun PaymentStatusIndicator(
    paymentStatus: PaymentStatus?,
    job: Job
) {
    when (paymentStatus) {
        PaymentStatus.NO_PAYMENT, PaymentStatus.PENDING, PaymentStatus.WAITING -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "⏳",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = if (job.status == JobStatus.APPROVED) {
                        "Payment pending final confirmation..."
                    } else {
                        "Waiting for employer to fund escrow..."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        PaymentStatus.PAID -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "✅",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Payment confirmed! Ready to start.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        PaymentStatus.RELEASED -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "💰",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Payment released to your wallet!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        PaymentStatus.DISPUTED -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "⚠️",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Payment in dispute",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        PaymentStatus.FAILED -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "❌",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Payment failed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        PaymentStatus.REFUNDED -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "↩️",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Payment refunded",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        null -> {
            if (job.status in setOf(JobStatus.ACCEPTED, JobStatus.APPROVED)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "🔄",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Loading payment status...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        else -> Unit
    }
}

// ─────────────────────────────────────────────────────────────
// Job Action Buttons
// ─────────────────────────────────────────────────────────────
@Composable
private fun JobActionButtons(
    job: Job,
    paymentStatus: PaymentStatus?,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onCounter: (Double) -> Unit,
    onStart: () -> Unit,
    onComplete: () -> Unit,
    onWithdraw: () -> Unit,
    onViewReceipt: () -> Unit,
    onViewDetails: () -> Unit
) {
    when (job.status) {
        JobStatus.PENDING -> {
            PendingJobActions(
                onAccept = onAccept,
                onReject = onReject,
                onCounter = onCounter
            )
        }
        JobStatus.ACCEPTED -> {
            AcceptedJobActions(
                paymentStatus = paymentStatus,
                onStart = onStart
            )
        }
        JobStatus.IN_PROGRESS -> {
            InProgressJobActions(onComplete = onComplete)
        }
        JobStatus.COMPLETED -> {
            CompletedJobActions(
                onViewReceipt = onViewReceipt,
                onViewDetails = onViewDetails
            )
        }
        JobStatus.APPROVED -> {
            ApprovedJobActions(
                onWithdraw = onWithdraw,
                onViewReceipt = onViewReceipt,
                onViewDetails = onViewDetails
            )
        }
        JobStatus.REJECTED, JobStatus.CANCELLED, JobStatus.CLIENT_CANCELLED -> {
            // No actions - just display the job
            Unit
        }
        else -> Unit
    }
}

// ─────────────────────────────────────────────────────────────
// Individual Action Components
// ─────────────────────────────────────────────────────────────

@Composable
private fun PendingJobActions(
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onCounter: (Double) -> Unit
) {
    var showCounterField by remember { mutableStateOf(false) }
    var counterPrice by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onAccept,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) { Text("Accept") }

            OutlinedButton(
                onClick = onReject,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) { Text("Reject") }
        }

        TextButton(
            onClick = { showCounterField = !showCounterField },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (showCounterField) "Cancel Counter Offer" else "Make Counter Offer")
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
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        counterPrice.toDoubleOrNull()?.let { onCounter(it) }
                        showCounterField = false
                    },
                    enabled = counterPrice.toDoubleOrNull() != null,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) { Text("Send") }
            }
        }
    }
}

@Composable
private fun AcceptedJobActions(
    paymentStatus: PaymentStatus?,
    onStart: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (paymentStatus == PaymentStatus.PAID) {
            Button(
                onClick = onStart,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) { Text("Start Job") }
        } else {
            Text(
                text = "⏳ Waiting for employer to fund escrow...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun InProgressJobActions(onComplete: () -> Unit) {
    Button(
        onClick = onComplete,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
    ) { Text("Mark Complete") }
}

@Composable
private fun CompletedJobActions(
    onViewReceipt: () -> Unit,
    onViewDetails: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "✅ Job completed — waiting for employer confirmation.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = onViewDetails,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Details") }
            OutlinedButton(
                onClick = onViewReceipt,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Receipt") }
        }
    }
}

@Composable
private fun ApprovedJobActions(
    onWithdraw: () -> Unit,
    onViewReceipt: () -> Unit,
    onViewDetails: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "✅ Employer confirmed! Payment is ready to withdraw.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Button(
            onClick = onWithdraw,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.AccountBalanceWallet,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Withdraw to Wallet")
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = onViewDetails,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Details") }
            OutlinedButton(
                onClick = onViewReceipt,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Receipt") }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Helper Functions
// ─────────────────────────────────────────────────────────────
private fun formatPrice(amount: Double): String {
    return String.format("%,.2f", amount)
}

@Composable
private fun statusAccentColor(status: JobStatus) = when (status) {
    JobStatus.PENDING -> MaterialTheme.colorScheme.tertiaryContainer
    JobStatus.ACCEPTED, JobStatus.APPROVED -> MaterialTheme.colorScheme.primary
    JobStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
    JobStatus.COMPLETED -> MaterialTheme.colorScheme.secondary
    JobStatus.REJECTED -> MaterialTheme.colorScheme.error
    JobStatus.CANCELLED, JobStatus.CLIENT_CANCELLED -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.secondaryContainer
}