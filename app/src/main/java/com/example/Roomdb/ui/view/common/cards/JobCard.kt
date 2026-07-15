package com.example.Roomdb.ui.view.common.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    counterpartyLabel: String,
    isActionInProgress: Boolean,
    paymentStatus: PaymentStatus? = null,
    actions: @Composable () -> Unit
) {
    val accentColor = statusAccentColor(job.status)

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.fillMaxWidth()) {
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
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        job.description,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    StatusChip(job.status, paymentStatus)
                }

                job.location?.let {
                    InfoRow(icon = Icons.Outlined.LocationOn, text = it)
                }
                job.scheduledDate?.let {
                    InfoRow(icon = Icons.Outlined.CalendarToday, text = it)
                }

                val displayPrice = job.negotiatedPrice ?: job.jobPrice ?: job.totalCost
                displayPrice?.let {
                    Text(
                        "KSh ${it.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                job.paymentAmount?.let {
                    if (it > 0 && job.status in setOf(JobStatus.ACCEPTED, JobStatus.APPROVED)) {
                        Text(
                            "💰 Escrow Amount: KSh ${it.toInt()}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                job.mpesaReceiptNumber?.let {
                    Text(
                        "📱 M-Pesa Receipt: $it",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                job.escrowMessage?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Pass job to PaymentStatusIndicator
                if (job.status in setOf(JobStatus.ACCEPTED, JobStatus.APPROVED)) {
                    PaymentStatusIndicator(
                        paymentStatus = paymentStatus,
                        job = job  // Pass the job parameter
                    )
                }

                if (job.negotiatedPrice != null && job.status == JobStatus.PENDING) {
                    Text(
                        "Counter-offer pending",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    )
                }

                job.rejectionReason?.let {
                    Text(
                        "Rejected: $it",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                job.cancellationReason?.let {
                    Text(
                        "Cancelled: $it",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                if (isActionInProgress) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                } else {
                    actions()
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatusChip(status: JobStatus, paymentStatus: PaymentStatus? = null) {
    val (containerColor, contentColor, label) = when (status) {
        JobStatus.ACCEPTED, JobStatus.APPROVED -> {
            when (paymentStatus) {
                PaymentStatus.PAID ->
                    Triple(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.primary,
                        "PAYMENT CONFIRMED"
                    )
                PaymentStatus.NO_PAYMENT ->
                    Triple(
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.error,
                        "AWAITING PAYMENT"
                    )
                PaymentStatus.PENDING, PaymentStatus.WAITING ->
                    Triple(
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.error,
                        "PAYMENT PENDING"
                    )
                PaymentStatus.RELEASED ->
                    Triple(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.primary,
                        "PAYMENT RELEASED"
                    )
                PaymentStatus.DISPUTED ->
                    Triple(
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.error,
                        "PAYMENT DISPUTED"
                    )
                PaymentStatus.FAILED ->
                    Triple(
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.error,
                        "PAYMENT FAILED"
                    )
                PaymentStatus.REFUNDED ->
                    Triple(
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.error,
                        "PAYMENT REFUNDED"
                    )
                else ->
                    Triple(
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.onSecondaryContainer,
                        status.name
                    )
            }
        }
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

@Composable
private fun PaymentStatusIndicator(
    paymentStatus: PaymentStatus?,
    job: Job  // Added job parameter
) {
    when (paymentStatus) {
        PaymentStatus.NO_PAYMENT -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "⏳",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Waiting for employer to fund escrow...",
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
        PaymentStatus.PENDING, PaymentStatus.WAITING -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "⏳",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Payment in progress...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
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
                    text = "Payment released to worker",
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
            // Now 'job' is accessible here
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
        else -> {
            // Unknown payment status
        }
    }
}

@Composable
private fun statusAccentColor(status: JobStatus) = when (status) {
    JobStatus.ACCEPTED, JobStatus.APPROVED -> MaterialTheme.colorScheme.primary
    JobStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
    JobStatus.COMPLETED -> MaterialTheme.colorScheme.secondary
    JobStatus.REJECTED -> MaterialTheme.colorScheme.error
    JobStatus.CANCELLED, JobStatus.CLIENT_CANCELLED -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.tertiaryContainer
}