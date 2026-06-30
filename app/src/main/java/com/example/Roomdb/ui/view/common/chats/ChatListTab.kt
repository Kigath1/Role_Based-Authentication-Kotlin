package com.example.Roomdb.ui.view.common.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Roomdb.data.model.employer.RecentConversation
import com.example.Roomdb.domain.utils.DateFormatter
import com.example.Roomdb.ui.theme.*
import com.example.Roomdb.viewmodel.common.chats.ChatListViewModel

@Composable
fun ChatListTab(
    viewModel: ChatListViewModel,
    onOpenChat: (recipientId: String, recipientName: String) -> Unit,
    modifier: Modifier = Modifier,

    emptyStateTitle: String = "No messages yet",
    emptyStateBody: String = "Your conversations will appear here."
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
            EmptyConversationsState(title = emptyStateTitle, body = emptyStateBody)
        }
        else -> {
            LazyColumn(modifier = modifier.fillMaxSize()) {
                items(uiState.conversations) { conversation ->
                    ConversationRow(
                        conversation = conversation,
                        onClick = { onOpenChat(conversation.otherUserId, conversation.otherUserName) }
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
            modifier = Modifier.size(48.dp).clip(CircleShape).background(KKBlueLight),
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
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(KKBlue))
        }
    }
}

@Composable
private fun EmptyConversationsState(title: String, body: String) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Outlined.Chat,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                body,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}