package com.example.Roomdb.ui.view.worker


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.Roomdb.viewmodel.worker.WorkerOnboardingViewModel

@Composable
fun WorkerOnboardingScreen(viewModel: WorkerOnboardingViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = when (state.step) {
                WorkerOnboardingViewModel.Step.BASIC_INFO -> "Basic info"
                WorkerOnboardingViewModel.Step.ADDITIONAL_INFO -> "Additional info"
                WorkerOnboardingViewModel.Step.DOCUMENT_UPLOAD -> "Upload a document"
                WorkerOnboardingViewModel.Step.DONE -> "All set"
            },
            style = MaterialTheme.typography.headlineSmall
        )

        when (state.step) {
            WorkerOnboardingViewModel.Step.BASIC_INFO -> {
                OutlinedTextField(state.fullName, viewModel::onFullNameChange, label = { Text("Full name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(state.phoneNumber, viewModel::onPhoneChange, label = { Text("Phone number") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(state.location, viewModel::onLocationChange, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(state.category, viewModel::onCategoryChange, label = { Text("Category (e.g. Plumbing)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(state.experienceYears, viewModel::onExperienceChange, label = { Text("Years of experience") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(state.hourlyRate, viewModel::onHourlyRateChange, label = { Text("Hourly rate (KES)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(state.skillsRaw, viewModel::onSkillsChange, label = { Text("Skills (comma separated)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(state.bio, viewModel::onBioChange, label = { Text("Bio (optional)") }, modifier = Modifier.fillMaxWidth())

                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                Button(onClick = viewModel::submitBasicInfo, enabled = !state.isLoading, modifier = Modifier.fillMaxWidth()) {
                    if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    else Text("Next")
                }
            }

            WorkerOnboardingViewModel.Step.ADDITIONAL_INFO -> {
                OutlinedTextField(state.availabilityDetails, viewModel::onAvailabilityChange, label = { Text("Availability") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(state.preferredLocationsRaw, viewModel::onPreferredLocationsChange, label = { Text("Preferred locations (comma separated)") }, modifier = Modifier.fillMaxWidth())

                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = viewModel::goBack) { Text("Back") }
                    Button(onClick = viewModel::submitAdditionalInfo, enabled = !state.isLoading, modifier = Modifier.weight(1f)) {
                        if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        else Text("Next")
                    }
                }
            }

            WorkerOnboardingViewModel.Step.DOCUMENT_UPLOAD -> {
                // Wire this button to a file/photo picker in your actual app;
                // it's a placeholder call demonstrating the use case wiring.
                Text("Upload an ID or certificate to finish setting up your profile.")
                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = viewModel::goBack) { Text("Back") }
                    TextButton(onClick = viewModel::skipDocumentUpload) { Text("Skip for now") }
                }
            }

            WorkerOnboardingViewModel.Step.DONE -> {
                Text("Your profile is pending review.")
            }
        }
    }
}