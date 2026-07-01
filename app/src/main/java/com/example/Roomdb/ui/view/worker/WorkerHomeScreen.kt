package com.example.Roomdb.ui.view.worker

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.Roomdb.ui.theme.*
import com.example.Roomdb.viewmodel.auth.AuthViewModel
import com.example.Roomdb.viewmodel.common.chats.ChatListViewModel
import com.example.Roomdb.viewmodel.worker.WorkerDashboardViewModel
import androidx.activity.compose.BackHandler

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
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == WorkerTab.DASHBOARD,
                    onClick = { selectedTab = WorkerTab.DASHBOARD },
                    icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                    label = { Text(WorkerTab.DASHBOARD.label) }
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
                    label = { Text(WorkerTab.MESSAGES.label) }
                )
                NavigationBarItem(
                    selected = selectedTab == WorkerTab.JOBS,
                    onClick = { selectedTab = WorkerTab.JOBS },
                    icon = { Icon(Icons.Outlined.Work, contentDescription = null) },
                    label = { Text(WorkerTab.JOBS.label) }
                )
                NavigationBarItem(
                    selected = selectedTab == WorkerTab.PROFILE,
                    onClick = { selectedTab = WorkerTab.PROFILE },
                    icon = { Icon(Icons.Outlined.AccountCircle, contentDescription = null) },
                    label = { Text(WorkerTab.PROFILE.label) }
                )
            }
        }
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
                WorkerTab.JOBS -> WorkerJobsPlaceholder()
                WorkerTab.PROFILE -> WorkerProfilePlaceholder(onLogout = onLogout)
            }
        }
    }
}

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
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // ── Top profile header ──────────────────────────────────────────
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(56.dp).clip(CircleShape),
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
                        modifier = Modifier.fillMaxSize().background(KKBlueLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            uiState.name.take(2).uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = KKBlue
                        )
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Welcome back,", fontSize = 13.sp, color = KKTextMuted)
                Text(
                    uiState.name.ifBlank { "Worker" },
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = KKTextPrimary
                )
            }
        }

        // ── Quick-access cards ───────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DashboardCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Chat,
                label = "Messages",
                badgeCount = unreadMessagesCount,
                onClick = onMessagesClick
            )
            DashboardCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Work,
                label = "Job Requests",
                badgeCount = uiState.pendingJobRequestsCount,
                onClick = onJobsClick
            )
        }
        DashboardCard(
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Outlined.AccountCircle,
            label = "My Profile",
            badgeCount = 0,
            onClick = onProfileClick
        )

        // ── In-progress work section ─────────────────────────────────────
        Text("In Progress", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = KKTextPrimary)
        InProgressPlaceholder()
    }
}

@Composable
private fun DashboardCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    badgeCount: Int,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = KKBlueLight
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Column {
                Icon(icon, contentDescription = null, tint = KKBlue, modifier = Modifier.size(28.dp))
                Spacer(Modifier.height(8.dp))
                Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = KKTextPrimary)
            }
            if (badgeCount > 0) {
                Badge(modifier = Modifier.align(Alignment.TopEnd)) { Text("$badgeCount") }
            }
        }
    }
}

@Composable
private fun InProgressPlaceholder() {
    // Wired once job requests are built — placeholder per agreed sequencing
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "No active jobs right now.",
            fontSize = 13.sp,
            color = KKTextMuted
        )
    }
}

@Composable
private fun WorkerJobsPlaceholder() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Job requests coming soon", color = KKTextMuted)
    }
}

@Composable
private fun WorkerProfilePlaceholder(onLogout: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Profile screen coming soon", color = KKTextMuted)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onLogout) { Text("Logout") }
        }
    }
}
