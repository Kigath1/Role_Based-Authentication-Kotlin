package com.example.Roomdb.data.remote.model.employer

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val id: String,
    val senderId: String,
    val senderName: String,
    val receiverId: String,
    val receiverName: String,
    val content: String,
    val attachmentUrl: String? = null,
    val sentAt: String,
    val isRead: Boolean
)

@Serializable
data class SendMessageRequest(
    val senderId: String,
    val receiverId: String,
    val content: String,
    val attachmentUrl: String? = null
)

@Serializable
data class ConversationResponse(
    val content: List<MessageDto>,
    val totalPages: Int,
    val totalElements: Int,
    val last: Boolean,
    val first: Boolean,
    val empty: Boolean
)