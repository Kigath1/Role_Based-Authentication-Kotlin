package com.example.Roomdb.ui.view.common.cards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Roomdb.data.model.Job
import com.example.Roomdb.data.model.JobStatus

@Composable
fun JobCard(
    job: Job,
    counterpartyLabel: String, // "Worker" or "Client" — caller decides which name to show
    isActionInProgress: Boolean,
    actions: @Composable () -> Unit
) {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(job.description, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                StatusChip(job.status)
            }

            job.location?.let { Text("📍 $it", fontSize = 13.sp) }
            job.scheduledDate?.let { Text("📅 $it", fontSize = 13.sp) }

            val displayPrice = job.negotiatedPrice ?: job.jobPrice ?: job.totalCost
            displayPrice?.let {
                Text("KSh ${it.toInt()}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
            if (job.negotiatedPrice != null && job.status == JobStatus.PENDING) {
                Text("Counter-offer pending", fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary)
            }

            job.rejectionReason?.let { Text("Rejected: $it", fontSize = 12.sp, color = MaterialTheme.colorScheme.error) }
            job.cancellationReason?.let { Text("Cancelled: $it", fontSize = 12.sp, color = MaterialTheme.colorScheme.error) }

            if (isActionInProgress) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else {
                actions()
            }
        }
    }
}

@Composable
private fun StatusChip(status: JobStatus) {
    val color = when (status) {
        JobStatus.ACCEPTED, JobStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
        JobStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary
        JobStatus.REJECTED, JobStatus.CANCELLED -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.secondary // PENDING / UNKNOWN
    }
    AssistChip(onClick = {}, label = { Text(status.name) }, colors = AssistChipDefaults.assistChipColors(labelColor = color))
}