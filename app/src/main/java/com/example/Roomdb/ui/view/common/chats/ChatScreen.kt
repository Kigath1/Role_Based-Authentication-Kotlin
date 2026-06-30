package com.example.Roomdb.ui.view.common.chats


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Roomdb.data.model.employer.Message
import com.example.Roomdb.domain.utils.DateFormatter
import com.example.Roomdb.ui.theme.*
import com.example.Roomdb.viewmodel.common.chats.ChatViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(uiState.messages.lastIndex)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(KKBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = viewModel.recipientName.take(2).uppercase(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = KKBlue
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = viewModel.recipientName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(uiState.messages, key = { it.id }) { message ->
                            MessageBubble(
                                message = message,
                                isSent = message.senderId == uiState.currentUserId
                            )
                        }
                    }
                }
            }

            uiState.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.inputText,
                    onValueChange = { viewModel.onInputChanged(it) },
                    placeholder = { Text("Type a message...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4,
                    enabled = !uiState.isSending
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = { viewModel.sendMessage() },
                    enabled = uiState.inputText.isNotBlank() && !uiState.isSending,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (uiState.inputText.isNotBlank()) KKBlue
                            else Color(0xFFE0E0E0)
                        )
                ) {
                    if (uiState.isSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}



//@Composable
//private fun MessageBubble(
//    message: Message,
//    isSent: Boolean
//) {
//    Column(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalAlignment = if (isSent) Alignment.End else Alignment.Start
//    ) {
//        Box(
//            modifier = Modifier
//                .widthIn(max = 280.dp)
//                .clip(
//                    RoundedCornerShape(
//                        topStart = 16.dp,
//                        topEnd = 16.dp,
//                        bottomStart = if (isSent) 16.dp else 4.dp,
//                        bottomEnd = if (isSent) 4.dp else 16.dp
//                    )
//                )
//                .background(if (isSent) SentBubble else ReceivedBubble)
//                .padding(horizontal = 14.dp, vertical = 10.dp)
//        ) {
//            Column {
//                Text(
//                    text = message.content,
//                    fontSize = 14.sp,
//                    color = if (isSent) Color.White else KKTextPrimary,
//                    lineHeight = 20.sp
//                )
//                Spacer(Modifier.height(3.dp))
//                // ← DateFormatter replaces the old formatBubbleTime() private function
//                Text(
//                    text = DateFormatter.toBubbleTime(message.sentAt),
//                    fontSize = 10.sp,
//                    color = if (isSent) Color.White.copy(alpha = 0.7f) else KKTextMuted
//                )
//            }
//        }
//    }
//}