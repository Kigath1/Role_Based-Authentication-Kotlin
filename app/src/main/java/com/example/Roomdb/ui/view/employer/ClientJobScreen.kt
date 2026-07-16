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
        Box(Modifier.padding(padding).fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                SegmentedFilter(state, viewModel::setFilter)

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
                                paymentStatus = state.paymentStatusMap[job.id],
                                jobIsReleased = state.isReleased(job.id),
                                jobIsReviewed = state.isReviewed(job.id),
                                onFundEscrow = { viewModel.openFundingSheet(job.id) },
                                onReleaseEscrow = { viewModel.releaseEscrow(job.id) },
                                onReview = { viewModel.openReviewPrompt(job.id) },
                                onAcceptCounterOffer = { viewModel.acceptCounterOffer(job.id) },
                                onCancelJob = { viewModel.cancelJob(job.id) }
                            )
                        }
                    }
                }
            }

            // ── Fund escrow sheet ────────────────────────────────────────
            state.fundingJobId?.let { jobId ->
                FundingSheet(
                    onDismiss = { viewModel.dismissFundingSheet() },
                    onFund = { phoneNumber -> viewModel.fundEscrow(jobId, phoneNumber) }
                )
            }

            // ── STK waiting dialog ───────────────────────────────────────
            if (state.awaitingStkConfirmationJobId != null) {
                StkWaitingDialog(onDismiss = { viewModel.dismissStkWaitingDialog() })
            }

            // ── Review dialog ────────────────────────────────────────────
            state.reviewPromptJobId?.let { jobId ->
                val job = state.jobs.find { it.id == jobId }
                val workerProfileId = job?.worker?.id
                if (job != null && workerProfileId != null) {
                    ReviewDialog(
                        workerName = job.worker.fullName,
                        isSubmitting = state.actionInProgressJobId == jobId,
                        onDismiss = { viewModel.dismissReviewPrompt() },
                        onSubmit = { rating, comment ->
                            viewModel.submitReview(jobId, workerProfileId, rating, comment)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SegmentedFilter(
    state: com.example.Roomdb.viewmodel.employer.ClientJobsUiState,
    onSelect: (JobFilter) -> Unit
) {
    Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = state.filter == JobFilter.ACTIVE,
            onClick = { onSelect(JobFilter.ACTIVE) },
            label = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Active")
                    if (state.needsFundingCount > 0) {
                        Spacer(Modifier.width(4.dp))
                        Badge { Text(state.needsFundingCount.toString()) }
                    }
                }
            }
        )
        FilterChip(
            selected = state.filter == JobFilter.COMPLETED,
            onClick = { onSelect(JobFilter.COMPLETED) },
            label = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Completed")
                    val badgeCount = state.readyToReleaseCount + state.awaitingReviewCount
                    if (badgeCount > 0) {
                        Spacer(Modifier.width(4.dp))
                        Badge { Text(badgeCount.toString()) }
                    }
                }
            }
        )
    }
}