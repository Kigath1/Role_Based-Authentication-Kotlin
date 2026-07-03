package com.example.Roomdb.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Roomdb.data.remote.model.employer.JobRequest
import com.example.Roomdb.data.remote.model.employer.Worker
import com.example.Roomdb.ui.theme.KKTextPrimary
import com.example.Roomdb.ui.view.employer.JobRequestSheet
import com.example.Roomdb.ui.view.employer.WorkerListCard
import com.example.Roomdb.viewmodel.employer.ClientHomeViewModel
import com.example.Roomdb.viewmodel.employer.JobRequestViewModel
import com.example.Roomdb.viewmodel.employer.JobRequestUiState

@Composable
fun WorkersTab(
    viewModel: ClientHomeViewModel,
    jobRequestViewModel: JobRequestViewModel,
    currentUserId: String,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val jobRequestState by jobRequestViewModel.state.collectAsState()
    val workers = uiState.workers
    val isLoading = uiState.isLoading
    val error = uiState.error

    LaunchedEffect(Unit) {
        if (workers.isEmpty() && !isLoading) {
            viewModel.loadWorkers()
        }
    }

    // Auto-dismiss the sheet on a successful submit, then reset the ViewModel
    // so re-opening it for a different worker doesn't show stale "Success" state.
    LaunchedEffect(jobRequestState) {
        if (jobRequestState is JobRequestUiState.Success) {
            viewModel.dismissHireSheet()
            jobRequestViewModel.reset()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = uiState.currentLocation ?: "",
                onValueChange = { newLocation -> viewModel.updateLocation(newLocation.ifBlank { null }) },
                label = { Text("Location (empty = all)") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = { viewModel.loadWorkers(forceRefresh = true) }) { Text("Refresh") }
        }

        when {
            isLoading && workers.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null && workers.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: $error", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadWorkers(forceRefresh = true) }) { Text("Retry") }
                    }
                }
            }
            workers.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No workers found")
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(workers) { worker ->
                        WorkerListCard(
                            worker = worker,
                            onMessage = { viewModel.onMessageClicked(worker) },
                            onHire = { viewModel.onHireClicked(worker) } // full worker, not just id
                        )
                    }
                }
            }
        }
    }

    uiState.selectedWorkerForHire?.let { worker ->
        JobRequestSheet(
            worker = worker,
            jobRequestState = jobRequestState,
            onSubmit = { request ->
                jobRequestViewModel.submit(
                    clientId = currentUserId,
                    workerUserId = request.workerUserId,
                    description = request.description,
                    location = request.location,
                    scheduledDate = request.scheduledDate,
                    budget = request.estimatedBudget
                )
            },
            onDismiss = {
                viewModel.dismissHireSheet()
                jobRequestViewModel.reset()
            }
        )
    }
}
