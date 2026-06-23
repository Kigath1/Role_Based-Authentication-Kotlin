package com.example.Roomdb.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.Roomdb.ui.view.employer.HomeTab
import com.example.Roomdb.ui.view.employer.ProfileTab
import com.example.Roomdb.viewmodel.employer.ClientHomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(
    viewModel: ClientHomeViewModel,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTabIndex = uiState.selectedTabIndex
    val tabs = listOf("Home", "Workers", "Profile")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Client Dashboard") },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Log out")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { viewModel.selectTab(index) },
                        text = { Text(title) }
                    )
                }
            }

            // Content based on selected tab
            when (selectedTabIndex) {
                0 -> HomeTab()
                1 -> WorkersTab(viewModel = viewModel, modifier = Modifier.weight(1f))
                2 -> ProfileTab()
            }
        }
    }
}