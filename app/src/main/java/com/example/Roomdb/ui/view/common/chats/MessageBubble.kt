package com.example.Roomdb.ui.view.common.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Roomdb.data.model.employer.Message
import com.example.Roomdb.domain.utils.DateFormatter
import com.example.Roomdb.ui.theme.*

@Composable
fun MessageBubble(
    message: Message,
    isSent: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isSent) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isSent) 16.dp else 4.dp,
                        bottomEnd = if (isSent) 4.dp else 16.dp
                    )
                )
                .background(if (isSent) SentBubble else ReceivedBubble)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column {
                Text(
                    text = message.content,
                    fontSize = 14.sp,
                    color = if (isSent) Color.White else KKTextPrimary,
                    lineHeight = 20.sp
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = DateFormatter.toBubbleTime(message.sentAt),
                    fontSize = 10.sp,
                    color = if (isSent) Color.White.copy(alpha = 0.7f) else KKTextMuted
                )
            }
        }
    }
}