package com.example.Roomdb.data.model.employer

data class Message(
    val id: String,
    val senderId: String,
    val senderName: String,
    val receiverId: String,
    val receiverName: String,
    val content: String,
    val attachmentUrl: String?,
    val sentAt: String,
    val isRead: Boolean
)

// Represents one row in the chat list — the latest message per contact
data class RecentConversation(
    val otherUserId: String,
    val otherUserName: String,
    val lastMessage: String,
    val sentAt: String,
    val isRead: Boolean
)