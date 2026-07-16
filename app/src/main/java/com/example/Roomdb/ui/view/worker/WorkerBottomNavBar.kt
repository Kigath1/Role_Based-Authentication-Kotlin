package com.example.Roomdb.ui.view.worker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class WorkerNavDestination { HOME, SEARCH, QUICK_ACTION, WALLET, PROFILE }

/**
 * Bottom navigation bar matching the dashboard / jobs-tab mockups:
 * Home, Search, a raised center action button, Wallet, Profile.
 * Purely presentational — wire [onDestinationSelected] to your
 * Navigation3 graph.
 */
@Composable
fun WorkerBottomNavBar(
    current: WorkerNavDestination,
    onDestinationSelected: (WorkerNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavIcon(
                icon = Icons.Outlined.Home,
                label = "Home",
                selected = current == WorkerNavDestination.HOME,
                onClick = { onDestinationSelected(WorkerNavDestination.HOME) }
            )
            NavIcon(
                icon = Icons.Outlined.Work,
                label = "Jobs",
                selected = current == WorkerNavDestination.SEARCH,
                onClick = { onDestinationSelected(WorkerNavDestination.SEARCH) }
            )
            // Reserved space for the raised center FAB drawn below.
            Spacer(modifier = Modifier.width(56.dp))
            NavIcon(
                icon = Icons.Outlined.AccountBalanceWallet,
                label = "Wallet",
                selected = current == WorkerNavDestination.WALLET,
                onClick = { onDestinationSelected(WorkerNavDestination.WALLET) }
            )
            NavIcon(
                icon = Icons.Outlined.Person,
                label = "Profile",
                selected = current == WorkerNavDestination.PROFILE,
                onClick = { onDestinationSelected(WorkerNavDestination.PROFILE) }
            )
        }

        // Raised center action button (e.g. quick job search / post action).
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-18).dp)
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { onDestinationSelected(WorkerNavDestination.QUICK_ACTION) }) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Quick action",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

@Composable
private fun NavIcon(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (selected) MaterialTheme.colorScheme.secondaryContainer else androidx.compose.ui.graphics.Color.Transparent
    val tint = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = if (selected) 16.dp else 10.dp, vertical = 8.dp)
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(24.dp)) {
            Icon(icon, contentDescription = label, tint = tint)
        }
    }
}