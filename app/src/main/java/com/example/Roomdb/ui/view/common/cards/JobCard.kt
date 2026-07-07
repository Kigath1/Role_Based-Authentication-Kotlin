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

@Composable
fun JobCard(
    job: Job,
    counterpartyLabel: String, // "Worker" or "Client" — caller decides which name to show
    isActionInProgress: Boolean,
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
            // Left accent strip — mirrors the mockup's border-l-4 treatment
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
                    StatusChip(job.status)
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
private fun StatusChip(status: JobStatus) {
    val (containerColor, contentColor) = when (status) {
        JobStatus.ACCEPTED, JobStatus.IN_PROGRESS ->
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) to MaterialTheme.colorScheme.primary
        JobStatus.COMPLETED ->
            MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        JobStatus.REJECTED, JobStatus.CANCELLED ->
            MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        else -> // PENDING / UNKNOWN
            MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }
    AssistChip(
        onClick = {},
        label = { Text(status.name, style = MaterialTheme.typography.labelSmall) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = containerColor,
            labelColor = contentColor
        ),
        border = null
    )
}

@Composable
private fun statusAccentColor(status: JobStatus) = when (status) {
    JobStatus.ACCEPTED, JobStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
    JobStatus.COMPLETED -> MaterialTheme.colorScheme.secondary
    JobStatus.REJECTED, JobStatus.CANCELLED -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.tertiaryContainer // PENDING = the mockup's "urgent" gold accent
}