package com.example.Roomdb.ui.view.employer.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Roomdb.ui.theme.*
import com.example.Roomdb.viewmodel.employer.ChatListViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.example.Roomdb.data.model.employer.RecentConversation
import com.example.Roomdb.domain.utils.DateFormatter

@Composable
fun ChatListTab(
    viewModel: ChatListViewModel,
    onOpenChat: (recipientId: String, recipientName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        uiState.error != null && uiState.conversations.isEmpty() -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Could not load conversations", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.loadConversations() }) { Text("Retry") }
                }
            }
        }
        uiState.conversations.isEmpty() -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No conversations yet.\nMessage a worker to get started.",
                    style = MaterialTheme.typography.bodyMedium)
            }
        }
        else -> {
            LazyColumn(modifier = modifier.fillMaxSize()) {
                items(uiState.conversations) { conversation ->
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            // ── TAP ROW → open chat with this worker ──────
                            .clickable {
                                onOpenChat(
                                    conversation.otherUserId,
                                    conversation.otherUserName
                                )
                            },
                        headlineContent = {
                            Text(
                                conversation.otherUserName,
                                fontWeight = if (!conversation.isRead) FontWeight.Bold
                                else FontWeight.Normal
                            )
                        },
                        supportingContent = {
                            Text(
                                conversation.lastMessage,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 13.sp
                            )
                        },
                        leadingContent = {
                            // Avatar initials
                            Surface(
                                modifier = Modifier.size(44.dp).clip(CircleShape),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        conversation.otherUserName.take(2).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        },
                        trailingContent = {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    conversation.sentAt.take(10), // date portion
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (!conversation.isRead) {
                                    Spacer(Modifier.height(4.dp))
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(8.dp)
                                    ) {}
                                }
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun ConversationRow(
    conversation: RecentConversation,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(KKBlueLight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = conversation.otherUserName.take(2).uppercase(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = KKBlue
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = conversation.otherUserName,
                    fontSize = 15.sp,
                    fontWeight = if (!conversation.isRead) FontWeight.Bold else FontWeight.Normal,
                    color = KKTextPrimary,
                    modifier = Modifier.weight(1f)
                )
                // ← DateFormatter replaces the old formatTime() private function
                Text(
                    text = DateFormatter.toListTime(conversation.sentAt),
                    fontSize = 11.sp,
                    color = KKTextMuted
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = conversation.lastMessage,
                fontSize = 13.sp,
                color = if (!conversation.isRead) KKTextPrimary else KKTextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = if (!conversation.isRead) FontWeight.Medium else FontWeight.Normal
            )
        }

        if (!conversation.isRead) {
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(KKBlue)
            )
        }
    }
}