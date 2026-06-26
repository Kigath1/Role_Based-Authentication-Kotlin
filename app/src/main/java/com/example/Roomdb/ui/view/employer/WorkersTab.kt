package com.example.Roomdb.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.Roomdb.ui.view.employer.WorkerListCard
import com.example.Roomdb.viewmodel.employer.ClientHomeViewModel

@Composable
fun WorkersTab(
    viewModel: ClientHomeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val workers = uiState.workers
    val isLoading = uiState.isLoading
    val error = uiState.error

    LaunchedEffect(Unit) {
        if (workers.isEmpty() && !isLoading) {
            viewModel.loadWorkers()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = uiState.currentLocation ?: "",
                onValueChange = { newLocation ->
                    viewModel.updateLocation(newLocation.ifBlank { null })
                },
                label = { Text("Location (empty = all)") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = { viewModel.loadWorkers(forceRefresh = true) }) {
                Text("Refresh")
            }
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
                        Button(onClick = { viewModel.loadWorkers(forceRefresh = true) }) {
                            Text("Retry")
                        }
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
                            // Pass full worker so onMessageClicked gets worker.userId
                            onMessage = { viewModel.onMessageClicked(worker) },
                            onHire = { viewModel.onHireClicked(worker.id) }
                        )
                    }
                }
            }
        }
    }
}