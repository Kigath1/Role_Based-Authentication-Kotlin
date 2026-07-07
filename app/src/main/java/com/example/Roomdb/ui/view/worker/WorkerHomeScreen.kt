package com.example.Roomdb.ui.view.worker

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.Roomdb.viewmodel.auth.AuthViewModel
import com.example.Roomdb.viewmodel.common.chats.ChatListViewModel
import com.example.Roomdb.viewmodel.worker.WorkerDashboardViewModel
import com.example.Roomdb.viewmodel.worker.WorkerProfileViewModel
import com.example.Roomdb.viewmodel.worker.WorkerJobsViewModel

private enum class WorkerTab(val label: String) {
    DASHBOARD("Dashboard"),
    MESSAGES("Messages"),
    JOBS("Jobs"),
    PROFILE("Profile")
}

@Composable
fun WorkerHomeScreen(
    authViewModel: AuthViewModel,
    dashboardViewModel: WorkerDashboardViewModel,
    chatListViewModel: ChatListViewModel,
    workerProfileViewModel: WorkerProfileViewModel,
    workerJobsViewModel: WorkerJobsViewModel,
    onOpenChat: (recipientId: String, recipientName: String) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(WorkerTab.DASHBOARD) }

    BackHandler(enabled = selectedTab != WorkerTab.DASHBOARD) {
        selectedTab = WorkerTab.DASHBOARD
    }

    val currentUser by authViewModel.currentUser.collectAsState()
    val chatState by chatListViewModel.uiState.collectAsState()
    val unreadCount = chatState.conversations.count { !it.isRead }

    LaunchedEffect(currentUser) {
        currentUser?.let {
            dashboardViewModel.loadProfile(it.name ?: it.username, it.profilePictureUrl)
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    selected = selectedTab == WorkerTab.DASHBOARD,
                    onClick = { selectedTab = WorkerTab.DASHBOARD },
                    icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                    label = { Text(WorkerTab.DASHBOARD.label) },
                    colors = kaziNavItemColors()
                )
                NavigationBarItem(
                    selected = selectedTab == WorkerTab.MESSAGES,
                    onClick = { selectedTab = WorkerTab.MESSAGES },
                    icon = {
                        BadgedBox(
                            badge = { if (unreadCount > 0) Badge { Text("$unreadCount") } }
                        ) {
                            Icon(Icons.Outlined.Chat, contentDescription = null)
                        }
                    },
                    label = { Text(WorkerTab.MESSAGES.label) },
                    colors = kaziNavItemColors()
                )
                NavigationBarItem(
                    selected = selectedTab == WorkerTab.JOBS,
                    onClick = { selectedTab = WorkerTab.JOBS },
                    icon = { Icon(Icons.Outlined.Work, contentDescription = null) },
                    label = { Text(WorkerTab.JOBS.label) },
                    colors = kaziNavItemColors()
                )
                NavigationBarItem(
                    selected = selectedTab == WorkerTab.PROFILE,
                    onClick = { selectedTab = WorkerTab.PROFILE },
                    icon = { Icon(Icons.Outlined.AccountCircle, contentDescription = null) },
                    label = { Text(WorkerTab.PROFILE.label) },
                    colors = kaziNavItemColors()
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (selectedTab) {
                WorkerTab.DASHBOARD -> WorkerDashboardHub(
                    dashboardViewModel = dashboardViewModel,
                    unreadMessagesCount = unreadCount,
                    onMessagesClick = { selectedTab = WorkerTab.MESSAGES },
                    onJobsClick = { selectedTab = WorkerTab.JOBS },
                    onProfileClick = { selectedTab = WorkerTab.PROFILE }
                )
                WorkerTab.MESSAGES -> com.example.Roomdb.ui.view.common.chats.ChatListTab(
                    viewModel = chatListViewModel,
                    onOpenChat = onOpenChat,
                    emptyStateTitle = "No messages yet",
                    emptyStateBody = "Clients will reach out here once they're interested in your services."
                )
                WorkerTab.JOBS -> WorkerJobsTab(
                    viewModel = workerJobsViewModel,
                    currentUserId = currentUser?.userId ?: ""
                )
                WorkerTab.PROFILE -> WorkerProfileScreen(
                    viewModel = workerProfileViewModel,
                    currentUserId = currentUser?.userId ?: "",
                    onLogout = onLogout
                )
            }
        }
    }
}

@Composable
private fun kaziNavItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
    selectedTextColor = MaterialTheme.colorScheme.primary,
    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
)

// ─────────────────────────────────────────────────────────────
// Dashboard hub — restyled to the Kazi.Konnect visual language,
// using only the fields WorkerDashboardViewModel actually exposes.
// ─────────────────────────────────────────────────────────────

@Composable
private fun WorkerDashboardHub(
    dashboardViewModel: WorkerDashboardViewModel,
    unreadMessagesCount: Int,
    onMessagesClick: () -> Unit,
    onJobsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val uiState by dashboardViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Greeting header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (!uiState.profilePictureUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = uiState.profilePictureUrl,
                        contentDescription = "Profile picture",
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            uiState.name.take(2).uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    "Welcome back,",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    uiState.name.ifBlank { "Worker" },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Bento row — Messages + Job Requests
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DashboardBentoCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Chat,
                label = "Messages",
                badgeCount = unreadMessagesCount,
                onClick = onMessagesClick
            )
            DashboardBentoCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Work,
                label = "Job Requests",
                badgeCount = uiState.pendingJobRequestsCount,
                onClick = onJobsClick
            )
        }

        // Full-width profile card
        DashboardBentoCard(
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Outlined.AccountCircle,
            label = "My Profile",
            badgeCount = 0,
            onClick = onProfileClick,
            fullWidth = true
        )

        // Profile completion
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Profile completeness",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "${uiState.profileCompletionPercent}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { uiState.profileCompletionPercent / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
            if (uiState.profileCompletionPercent < 100) {
                TextButton(onClick = onProfileClick, contentPadding = PaddingValues(top = 8.dp)) {
                    Text("Complete your profile", color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Text(
            "In Progress",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        InProgressPlaceholder()
    }
}

@Composable
private fun DashboardBentoCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    badgeCount: Int,
    onClick: () -> Unit,
    fullWidth: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        if (fullWidth) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BentoIconBadge(icon)
                Spacer(Modifier.width(12.dp))
                Text(label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Column {
                BentoIconBadge(icon)
                Spacer(Modifier.height(12.dp))
                Text(label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
        if (badgeCount > 0) {
            Badge(
                containerColor = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.TopEnd)
            ) { Text("$badgeCount") }
        }
    }
}

@Composable
private fun BentoIconBadge(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
private fun InProgressPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "No active jobs right now.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}