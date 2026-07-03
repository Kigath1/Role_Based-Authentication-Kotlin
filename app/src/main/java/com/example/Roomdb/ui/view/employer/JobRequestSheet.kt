package com.example.Roomdb.ui.view.employer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Roomdb.data.model.JobRequest
import com.example.Roomdb.data.model.Worker
import com.example.Roomdb.ui.theme.KKBlue
import com.example.Roomdb.ui.theme.KKBorder
import com.example.Roomdb.ui.theme.KKTextMuted
import com.example.Roomdb.ui.theme.KKTextPrimary
import com.example.Roomdb.viewmodel.employer.JobRequestUiState
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobRequestSheet(
    worker: Worker,
    jobRequestState: JobRequestUiState,
    onSubmit: (JobRequest) -> Unit,
    onDismiss: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    var location by remember(worker.id) { mutableStateOf(worker.location) }
    var scheduledDate by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val isFormValid = description.isNotBlank() && location.isNotBlank() &&
            scheduledDate.matches(Regex("""\d{4}-\d{2}-\d{2}""")) && budget.toDoubleOrNull() != null

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Request ${worker.fullName}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KKTextPrimary)
            Text(
                "${worker.category} · ${worker.location} · KSh ${worker.hourlyRate.toInt()}/hr",
                fontSize = 12.sp,
                color = KKTextMuted
            )
            HorizontalDivider(color = KKBorder)

            RequestField(
                label = "Job description",
                value = description,
                onValueChange = { description = it },
                placeholder = "Describe what you need done…",
                leadingIcon = Icons.Outlined.Description
            )
            RequestField(
                label = "Your location",
                value = location,
                onValueChange = { location = it },
                placeholder = "e.g. Westlands, Nairobi",
                leadingIcon = Icons.Outlined.LocationOn
            )

            // Read-only field — tapping anywhere on it (or the icon) opens the picker.
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Scheduled date", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = KKTextMuted)
                OutlinedTextField(
                    value = scheduledDate,
                    onValueChange = {}, // no manual typing anymore
                    readOnly = true,
                    placeholder = { Text("Select a date", fontSize = 13.sp) },
                    leadingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Outlined.CalendarToday, contentDescription = "Pick date")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickableNoRipple { showDatePicker = true }
                )
            }

            RequestField(
                label = "Estimated budget (KSh)",
                value = budget,
                onValueChange = { budget = it },
                placeholder = "e.g. 3000",
                leadingIcon = Icons.Outlined.AttachMoney,
                keyboardType = KeyboardType.Number
            )

            if (jobRequestState is JobRequestUiState.Error) {
                Text(jobRequestState.message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            Button(
                onClick = {
                    onSubmit(
                        JobRequest(
                            workerUserId = worker.userId,
                            description = description,
                            location = location,
                            scheduledDate = scheduledDate,
                            estimatedBudget = budget.toDoubleOrNull() ?: 0.0
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = jobRequestState !is JobRequestUiState.Loading && isFormValid,
                colors = ButtonDefaults.buttonColors(containerColor = KKBlue)
            ) {
                if (jobRequestState is JobRequestUiState.Loading) {
                    CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Send Job Request", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showDatePicker) {
        val today = System.currentTimeMillis()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = today,
            selectableDates = object : SelectableDates {
                // Blocks past dates — a job can't be scheduled yesterday.
                override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis >= today - 86_400_000L
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        scheduledDate = formatToApiDate(millis)
                    }
                    showDatePicker = false
                }) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// DatePicker returns UTC millis regardless of device timezone — must format in UTC,
// otherwise Nairobi (UTC+3) can roll the date back by one day.
private fun formatToApiDate(utcMillis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(utcMillis)
}

@Composable
private fun RequestField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = KKTextMuted)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 13.sp) },
            leadingIcon = { Icon(leadingIcon, contentDescription = null) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Small helper so tapping anywhere on the date field opens the picker,
// not just the icon — without pulling in a full clickable-with-ripple import chain.
@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier =
    this.then(
        Modifier.clickable(
            indication = null,
            interactionSource = androidx.compose.runtime.remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
            onClick = onClick
        )
    )