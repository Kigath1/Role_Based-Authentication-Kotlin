package com.example.Roomdb.ui.view.worker


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

    // Same rule as elsewhere: fires only once a real user is present.
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotBlank()) viewModel.loadJobs(currentUserId)
    }
    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it); viewModel.clearError() }
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(state.filter == JobFilter.ACTIVE, { viewModel.setFilter(JobFilter.ACTIVE) }, label = { Text("Active") })
                FilterChip(state.filter == JobFilter.COMPLETED, { viewModel.setFilter(JobFilter.COMPLETED) }, label = { Text("Completed") })
            }

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                state.filteredJobs.isEmpty() -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(if (state.filter == JobFilter.ACTIVE) "No incoming requests yet." else "No completed jobs yet.")
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.filteredJobs, key = { it.id }) { job ->
                        var showCounterField by remember(job.id) { mutableStateOf(false) }
                        var counterPrice by remember(job.id) { mutableStateOf("") }

                        JobCard(
                            job = job,
                            counterpartyLabel = "Client",
                            isActionInProgress = state.actionInProgressJobId == job.id
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                when (job.status.name) {
                                    "PENDING" -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(onClick = { viewModel.acceptJob(job.id) }) { Text("Accept") }
                                        OutlinedButton(onClick = { viewModel.rejectJob(job.id) }) { Text("Reject") }
                                        OutlinedButton(onClick = { showCounterField = !showCounterField }) { Text("Counter") }
                                    }
                                    "ACCEPTED" -> Button(onClick = { viewModel.startJob(job.id) }) { Text("Start Job") }
                                    "IN_PROGRESS" -> Button(onClick = { viewModel.completeJob(job.id) }) { Text("Mark Complete") }
                                    else -> {}
                                }

                                if (showCounterField) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = counterPrice,
                                            onValueChange = { counterPrice = it },
                                            label = { Text("Your price (KSh)") },
                                            modifier = Modifier.weight(1f)
                                        )
                                        Button(
                                            onClick = {
                                                counterPrice.toDoubleOrNull()?.let { viewModel.counterOffer(job.id, it) }
                                                showCounterField = false
                                            },
                                            enabled = counterPrice.toDoubleOrNull() != null
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
    }
}