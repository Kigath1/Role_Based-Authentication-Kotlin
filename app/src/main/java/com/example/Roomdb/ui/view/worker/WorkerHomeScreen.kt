package com.example.Roomdb.ui.view.worker

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.Roomdb.data.model.Job
import com.example.Roomdb.data.model.JobStatus
import com.example.Roomdb.viewmodel.auth.AuthViewModel
import com.example.Roomdb.viewmodel.common.chats.ChatListViewModel
import com.example.Roomdb.viewmodel.worker.WalletViewModel
import com.example.Roomdb.viewmodel.worker.WorkerDashboardViewModel
import com.example.Roomdb.viewmodel.worker.WorkerJobsViewModel
import com.example.Roomdb.viewmodel.worker.WorkerProfileViewModel

private enum class WorkerTab(val label: String) {
    DASHBOARD("Dashboard"),
    MESSAGES("Messages"),
    JOBS("Jobs"),
    WALLET("Wallet"),
    PROFILE("Profile")
}

@Composable
fun WorkerHomeScreen(
    authViewModel: AuthViewModel,
    dashboardViewModel: WorkerDashboardViewModel,
    chatListViewModel: ChatListViewModel,
    workerProfileViewModel: WorkerProfileViewModel,
    workerJobsViewModel: WorkerJobsViewModel,
    walletViewModel: WalletViewModel,
    onOpenChat: (recipientId: String, recipientName: String) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(WorkerTab.DASHBOARD) }

    BackHandler(enabled = selectedTab != WorkerTab.DASHBOARD) {
        selectedTab = WorkerTab.DASHBOARD
    }

    val currentUser by authViewModel.currentUser.collectAsState()
    val chatState by chatListViewModel.uiState.collectAsState()
    val jobsState by workerJobsViewModel.uiState.collectAsState()
    val unreadCount = chatState.conversations.count { !it.isRead }

    LaunchedEffect(currentUser) {
        currentUser?.let {
            dashboardViewModel.loadProfile(it.name ?: it.username, it.profilePictureUrl)
            dashboardViewModel.loadCompletion(it.userId)
            workerJobsViewModel.loadJobs(it.userId)
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
                    selected = selectedTab == WorkerTab.WALLET,
                    onClick = { selectedTab = WorkerTab.WALLET },
                    icon = { Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null) },
                    label = { Text(WorkerTab.WALLET.label) },
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
                    activeAndPendingJobs = jobsState.jobs.filter { it.isActive },
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
                    currentUserId = currentUser?.userId ?: "",
                    onGoToWallet = { selectedTab = WorkerTab.WALLET }
                )
                WorkerTab.WALLET -> WalletScreen(
                    viewModel = walletViewModel
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
// Dashboard Hub
// ─────────────────────────────────────────────────────────────

@Composable
private fun WorkerDashboardHub(
    dashboardViewModel: WorkerDashboardViewModel,
    activeAndPendingJobs: List<Job>,
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
        GreetingHeader(
            name = uiState.name,
            profilePictureUrl = uiState.profilePictureUrl
        )

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
                badgeCount = activeAndPendingJobs.count { it.status == JobStatus.PENDING },
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

        // In Progress Section
        InProgressSection(
            jobs = activeAndPendingJobs,
            onViewAll = onJobsClick
        )
    }
}

@Composable
private fun GreetingHeader(
    name: String,
    profilePictureUrl: String?
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (!profilePictureUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = profilePictureUrl,
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
                        name.take(2).uppercase(),
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
                name.ifBlank { "Worker" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
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
                Text(
                    label,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Column {
                BentoIconBadge(icon)
                Spacer(Modifier.height(12.dp))
                Text(
                    label,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
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
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

// ─────────────────────────────────────────────────────────────
// In Progress Section
// ─────────────────────────────────────────────────────────────

@Composable
private fun InProgressSection(
    jobs: List<Job>,
    onViewAll: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "In Progress",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            if (jobs.isNotEmpty()) {
                TextButton(onClick = onViewAll) { Text("View all") }
            }
        }
        Spacer(Modifier.height(8.dp))

        if (jobs.isEmpty()) {
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
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                jobs.take(3).forEach { job ->
                    CompactJobRow(job = job, onClick = onViewAll)
                }
            }
        }
    }
}

@Composable
private fun CompactJobRow(job: Job, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                jobIconFor(job.description),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                job.description,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                "Client: ${job.client?.fullName ?: "—"}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        JobStatusPill(status = job.status)
    }
}

@Composable
private fun JobStatusPill(status: JobStatus) {
    val label = when (status) {
        JobStatus.PENDING -> "New"
        JobStatus.ACCEPTED, JobStatus.APPROVED -> "Accepted"
        JobStatus.IN_PROGRESS -> "In Progress"
        else -> status.name.lowercase().replaceFirstChar { it.uppercase() }
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Job Icon Helper
// ─────────────────────────────────────────────────────────────
internal fun jobIconFor(description: String): ImageVector {
    val d = description.lowercase()
    return when {
        "electric" in d -> Icons.Outlined.ElectricBolt
        "plumb" in d || "leak" in d -> Icons.Outlined.Plumbing
        "paint" in d -> Icons.Outlined.FormatPaint
        "clean" in d -> Icons.Outlined.CleaningServices
        else -> Icons.Outlined.Build
    }
}