package com.example.Roomdb.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.Roomdb.ui.view.employer.HomeTab
import com.example.Roomdb.ui.view.employer.ProfileTab
import com.example.Roomdb.ui.view.common.chats.ChatListTab
import com.example.Roomdb.viewmodel.common.chats.ChatListViewModel
import com.example.Roomdb.viewmodel.employer.ClientHomeViewModel
import com.example.Roomdb.viewmodel.employer.ClientProfileViewModel
import com.example.Roomdb.viewmodel.employer.JobRequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(
    viewModel: ClientHomeViewModel,
    chatListViewModel: ChatListViewModel,
    clientProfileViewModel: ClientProfileViewModel,   // was already missing here
    jobRequestViewModel: JobRequestViewModel,          // new
    currentUserId: String,
    currentUserEmail: String,
    onLogout: () -> Unit,
    onOpenChat: (recipientId: String, recipientName: String) -> Unit,
    onOpenJobRequests: () -> Unit
) {
    viewModel.onNavigateToChat = onOpenChat

    val uiState by viewModel.uiState.collectAsState()
    val selectedTabIndex = uiState.selectedTabIndex

    BackHandler(enabled = selectedTabIndex != 0) {
        viewModel.selectTab(0)
    }

    val tabs = listOf("Home", "Workers", "Messages", "Profile")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Client Dashboard") },
                actions = {
                    TextButton(onClick = onLogout) { Text("Log out") }
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

            when (selectedTabIndex) {
                0 -> HomeTab(onOpenJobRequests = onOpenJobRequests)
                1 -> WorkersTab(
                    viewModel = viewModel,
                    jobRequestViewModel = jobRequestViewModel,
                    currentUserId = currentUserId,
                    modifier = Modifier.weight(1f)
                )
                2 -> ChatListTab(
                    viewModel = chatListViewModel,
                    onOpenChat = onOpenChat,
                    emptyStateTitle = "No messages yet",
                    emptyStateBody = "Go to the Workers tab and tap Message on any worker to start a conversation."
                )
                3 -> ProfileTab(
                    viewModel = clientProfileViewModel,
                    currentUserId = currentUserId,
                    currentUserEmail = currentUserEmail,
                    onLogout = onLogout
                )
            }
        }
    }
}