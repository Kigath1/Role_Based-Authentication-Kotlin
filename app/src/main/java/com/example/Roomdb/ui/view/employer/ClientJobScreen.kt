package com.example.Roomdb.ui.view.employer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.Roomdb.ui.view.common.cards.EmployerJobCard
import com.example.Roomdb.viewmodel.employer.ClientJobsViewModel
import com.example.Roomdb.viewmodel.employer.JobFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientJobsScreen(
    viewModel: ClientJobsViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it); viewModel.clearError() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Requests") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            SegmentedFilter(selected = state.filter, onSelect = viewModel::setFilter)

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                state.filteredJobs.isEmpty() -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(if (state.filter == JobFilter.ACTIVE) "No active requests yet." else "No completed requests yet.")
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.filteredJobs, key = { it.id }) { job ->
                        EmployerJobCard(
                            job = job,
                            isActionInProgress = state.actionInProgressJobId == job.id,
                            onAcceptCounterOffer = { viewModel.acceptCounterOffer(job.id) },
                            onCancelJob = { viewModel.cancelJob(job.id) },
                            onViewReceipt = { /* Navigate to receipt */ },
                            onViewDetails = { /* Navigate to job details */ },
                            onReleasePayment = { /* Handle release payment */ },
                            onRefundPayment = { /* Handle refund payment */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SegmentedFilter(selected: JobFilter, onSelect: (JobFilter) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(selected == JobFilter.ACTIVE, { onSelect(JobFilter.ACTIVE) }, label = { Text("Active") })
        FilterChip(selected == JobFilter.COMPLETED, { onSelect(JobFilter.COMPLETED) }, label = { Text("Completed") })
    }
}