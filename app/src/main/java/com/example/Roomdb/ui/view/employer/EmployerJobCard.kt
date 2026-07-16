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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Roomdb.data.model.Job
import com.example.Roomdb.data.model.JobStatus
import com.example.Roomdb.data.model.PaymentStatus

@Composable
fun EmployerJobCard(
    job: Job,
    isActionInProgress: Boolean,
    paymentStatus: PaymentStatus? = null,
    onAcceptCounterOffer: () -> Unit = {},
    onCancelJob: () -> Unit = {},
    onViewReceipt: () -> Unit = {},
    onViewDetails: () -> Unit = {},
    onReleasePayment: () -> Unit = {},
    onRefundPayment: () -> Unit = {}
) {
    val accentColor = statusAccentColor(job.status)
    val isFinished = job.status in setOf(JobStatus.COMPLETED, JobStatus.APPROVED)

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
                // ── Header ──
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
                    EmployerStatusChip(job.status, paymentStatus)
                }

                // ── Worker Name ──
                job.worker?.fullName?.let { workerName ->
                    EmployerInfoRow(
                        icon = Icons.Outlined.Person,
                        text = "Worker: $workerName"
                    )
                }

                // ── Location ──
                job.location?.let { location ->
                    EmployerInfoRow(
                        icon = Icons.Outlined.LocationOn,
                        text = location
                    )
                }

                // ── Scheduled Date ──
                job.scheduledDate?.let { date ->
                    EmployerInfoRow(
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

                // ── Payment Amount ──
                if (job.status in setOf(JobStatus.COMPLETED, JobStatus.APPROVED)) {
                    job.paymentAmount?.let { amount ->
                        EmployerInfoRow(
                            icon = Icons.Outlined.Payments,
                            text = "Payment: KSh ${formatPrice(amount)}"
                        )
                    }
                }

                // ── Payment Status ──
                if (job.status in setOf(JobStatus.ACCEPTED, JobStatus.APPROVED)) {
                    EmployerPaymentStatusIndicator(paymentStatus)
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

                // ── Employer Actions ──
                if (isActionInProgress) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                } else {
                    EmployerJobActions(
                        job = job,
                        paymentStatus = paymentStatus,
                        onAcceptCounterOffer = onAcceptCounterOffer,
                        onCancelJob = onCancelJob,
                        onViewReceipt = onViewReceipt,
                        onViewDetails = onViewDetails,
                        onReleasePayment = onReleasePayment,
                        onRefundPayment = onRefundPayment
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Employer Status Chip
// ─────────────────────────────────────────────────────────────
@Composable
private fun EmployerStatusChip(
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
                        "ESCROW FUNDED"
                    )
                PaymentStatus.NO_PAYMENT, PaymentStatus.PENDING ->
                    Triple(
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.error,
                        "AWAITING FUNDING"
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
                "PAYMENT RELEASED"
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
// Employer Info Row
// ─────────────────────────────────────────────────────────────
@Composable
private fun EmployerInfoRow(
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
// Employer Payment Status Indicator
// ─────────────────────────────────────────────────────────────
@Composable
private fun EmployerPaymentStatusIndicator(paymentStatus: PaymentStatus?) {
    when (paymentStatus) {
        PaymentStatus.NO_PAYMENT, PaymentStatus.PENDING -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("⏳")
                Text(
                    "Payment pending - waiting for you to fund escrow",
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
                Text("✅")
                Text(
                    "Payment confirmed - escrow funded",
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
                Text("💰")
                Text(
                    "Payment released to worker",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        PaymentStatus.REFUNDED -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("↩️")
                Text(
                    "Payment refunded",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        null -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("🔄")
                Text(
                    "Loading payment status...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        else -> Unit
    }
}

// ─────────────────────────────────────────────────────────────
// Employer Job Actions
// ─────────────────────────────────────────────────────────────
@Composable
private fun EmployerJobActions(
    job: Job,
    paymentStatus: PaymentStatus?,
    onAcceptCounterOffer: () -> Unit,
    onCancelJob: () -> Unit,
    onViewReceipt: () -> Unit,
    onViewDetails: () -> Unit,
    onReleasePayment: () -> Unit,
    onRefundPayment: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        when (job.status) {
            JobStatus.PENDING -> {
                // Pending - show counter offer and cancel
                if (job.negotiatedPrice != null) {
                    Button(
                        onClick = onAcceptCounterOffer,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Accept Counter-Offer: KSh ${formatPrice(job.negotiatedPrice ?: 0.0)}")
                    }
                }
                OutlinedButton(
                    onClick = onCancelJob,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Cancel Request") }
            }
            JobStatus.ACCEPTED -> {
                // Accepted - show payment status and actions
                when (paymentStatus) {
                    PaymentStatus.NO_PAYMENT, PaymentStatus.PENDING -> {
                        Text(
                            "⏳ Waiting for you to fund escrow",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(
                            onClick = onViewDetails,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("Fund Escrow Now") }
                    }
                    PaymentStatus.PAID -> {
                        Text(
                            "✅ Escrow funded - job in progress",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    else -> Unit
                }
            }
            JobStatus.IN_PROGRESS -> {
                Text(
                    "🔨 Worker is working on the job",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            JobStatus.COMPLETED -> {
                // Completed - waiting for employer to release
                Text(
                    "✅ Job completed - review and release payment",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onReleasePayment,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Release Payment") }
                    OutlinedButton(
                        onClick = onRefundPayment,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) { Text("Refund") }
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
            JobStatus.APPROVED -> {
                // Approved - funds released
                Text(
                    "💰 Payment released to worker",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
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
            JobStatus.REJECTED -> {
                Text(
                    "❌ Job was rejected by worker",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            JobStatus.CANCELLED, JobStatus.CLIENT_CANCELLED -> {
                Text(
                    "🚫 Job was cancelled",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> Unit
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