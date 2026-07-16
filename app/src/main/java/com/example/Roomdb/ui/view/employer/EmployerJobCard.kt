package com.example.Roomdb.ui.view.common.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Roomdb.data.model.Job
import com.example.Roomdb.data.model.JobStatus
import com.example.Roomdb.data.model.PaymentStatus

/**
 * jobIsReleased / jobIsReviewed are derived in the ViewModel (paymentStatusMap +
 * reviewedJobIds), NOT from job.status — there is no post-release job status.
 * "APPROVED" in JobStatus is a worker-profile-verification status set by an
 * admin; it is unrelated to job/escrow lifecycle and must never be used here.
 */
@Composable
fun EmployerJobCard(
    job: Job,
    isActionInProgress: Boolean,
    paymentStatus: PaymentStatus?,
    jobIsReleased: Boolean,
    jobIsReviewed: Boolean,
    onFundEscrow: () -> Unit,
    onReleaseEscrow: () -> Unit,
    onReview: () -> Unit,
    onAcceptCounterOffer: () -> Unit,
    onCancelJob: () -> Unit
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
                Modifier.padding(16.dp).weight(1f),
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
                    EmployerStatusChip(job.status, paymentStatus, jobIsReleased)
                }

                job.worker?.fullName?.let {
                    InfoRow(icon = Icons.Outlined.Person, text = "Worker: $it")
                }
                job.location?.let {
                    InfoRow(icon = Icons.Outlined.LocationOn, text = it)
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
                        jobIsReleased = jobIsReleased,
                        jobIsReviewed = jobIsReviewed,
                        onFundEscrow = onFundEscrow,
                        onReleaseEscrow = onReleaseEscrow,
                        onReview = onReview,
                        onAcceptCounterOffer = onAcceptCounterOffer,
                        onCancelJob = onCancelJob
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EmployerStatusChip(status: JobStatus, paymentStatus: PaymentStatus?, jobIsReleased: Boolean) {
    val (container, content, label) = when {
        status == JobStatus.PENDING ->
            Triple(MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.onSecondaryContainer, "PENDING")

        status == JobStatus.ACCEPTED && paymentStatus == PaymentStatus.PAID ->
            Triple(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                MaterialTheme.colorScheme.primary, "ESCROW FUNDED")

        status == JobStatus.ACCEPTED ->
            Triple(MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.onSecondaryContainer, "AWAITING YOUR PAYMENT")

        status == JobStatus.IN_PROGRESS ->
            Triple(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                MaterialTheme.colorScheme.primary, "IN PROGRESS")

        status == JobStatus.COMPLETED && jobIsReleased ->
            Triple(MaterialTheme.colorScheme.tertiaryContainer,
                MaterialTheme.colorScheme.onTertiaryContainer, "PAID OUT")

        status == JobStatus.COMPLETED ->
            Triple(MaterialTheme.colorScheme.tertiaryContainer,
                MaterialTheme.colorScheme.onTertiaryContainer, "COMPLETED")

        status == JobStatus.REJECTED ->
            Triple(MaterialTheme.colorScheme.errorContainer,
                MaterialTheme.colorScheme.onErrorContainer, "REJECTED")

        status == JobStatus.CANCELLED || status == JobStatus.CLIENT_CANCELLED ->
            Triple(MaterialTheme.colorScheme.errorContainer,
                MaterialTheme.colorScheme.onErrorContainer, "CANCELLED")

        else ->
            Triple(MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.onSecondaryContainer, status.name)
    }

    AssistChip(
        onClick = {},
        label = { Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium) },
        colors = AssistChipDefaults.assistChipColors(containerColor = container, labelColor = content),
        border = null
    )
}

@Composable
private fun EmployerJobActions(
    job: Job,
    paymentStatus: PaymentStatus?,
    jobIsReleased: Boolean,
    jobIsReviewed: Boolean,
    onFundEscrow: () -> Unit,
    onReleaseEscrow: () -> Unit,
    onReview: () -> Unit,
    onAcceptCounterOffer: () -> Unit,
    onCancelJob: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        when (job.status) {
            JobStatus.PENDING -> {
                if (job.negotiatedPrice != null) {
                    // Worker sent a counter-offer — employer can accept it or cancel.
                    Text(
                        "The worker countered at KSh ${job.negotiatedPrice.toInt()}.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onAcceptCounterOffer,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) { Text("Accept Offer") }
                        OutlinedButton(
                            onClick = onCancelJob,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) { Text("Cancel") }
                    }
                } else {
                    Text(
                        "⏳ Waiting for worker to respond...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedButton(
                        onClick = onCancelJob,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) { Text("Cancel Request") }
                }
            }

            JobStatus.ACCEPTED -> {
                when (paymentStatus) {
                    PaymentStatus.PAID -> {
                        Text(
                            "✅ Escrow funded — job is starting.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    PaymentStatus.PENDING, PaymentStatus.WAITING -> {
                        Text(
                            "⏳ Payment in progress — check your phone.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    else -> {
                        Text(
                            "The worker accepted this job. Fund the escrow to start it.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(
                            onClick = onFundEscrow,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(Icons.Outlined.Payments, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Fund Escrow")
                        }
                    }
                }
            }

            JobStatus.IN_PROGRESS -> {
                Text(
                    "🔨 Worker is on the job.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            JobStatus.COMPLETED -> {
                when {
                    !jobIsReleased -> {
                        Text(
                            "✅ Worker marked this job complete. Release funds to pay them.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Button(
                            onClick = onReleaseEscrow,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) { Text("Release Funds") }
                    }
                    !jobIsReviewed -> {
                        Text(
                            "💰 Funds released to the worker.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Button(
                            onClick = onReview,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            )
                        ) {
                            Icon(Icons.Outlined.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Review & Rate Worker")
                        }
                    }
                    else -> {
                        Text(
                            "✅ Job complete and reviewed.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            JobStatus.REJECTED -> {
                Text("❌ Job was rejected by worker",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
            }

            JobStatus.CANCELLED, JobStatus.CLIENT_CANCELLED -> {
                Text("🚫 Job was cancelled",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
            }

            else -> Unit
        }
    }
}

@Composable
private fun statusAccentColor(status: JobStatus) = when (status) {
    JobStatus.ACCEPTED, JobStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
    JobStatus.COMPLETED -> MaterialTheme.colorScheme.secondary
    JobStatus.REJECTED, JobStatus.CANCELLED, JobStatus.CLIENT_CANCELLED -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.tertiaryContainer
}