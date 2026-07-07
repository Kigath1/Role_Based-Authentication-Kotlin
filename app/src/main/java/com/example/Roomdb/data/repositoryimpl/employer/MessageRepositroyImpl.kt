package com.example.Roomdb.data.repositoryimpl.employer

import android.util.Log
import com.example.Roomdb.api.employer.MessageApiService
import com.example.Roomdb.data.model.employer.Message
import com.example.Roomdb.data.model.employer.RecentConversation
import com.example.Roomdb.data.remote.model.employer.MessageDto
import com.example.Roomdb.data.remote.model.employer.SendMessageRequest
import com.example.Roomdb.domain.repository.employer.MessageRepository

class MessageRepositoryImpl(
    private val api: MessageApiService
) : MessageRepository {

    override suspend fun getRecentConversations(userId: String): Result<List<RecentConversation>> {
        return try {
            val response = api.getRecentConversations(userId)
            val conversations = response.map { dto ->
                val isSender = dto.senderId == userId
                RecentConversation(
                    otherUserId = if (isSender) dto.receiverId else dto.senderId,
                    otherUserName = if (isSender) dto.receiverName else dto.senderName,
                    lastMessage = dto.content,
                    sentAt = dto.sentAt,
                    isRead = if (isSender) true else dto.isRead
                )
            }
            Result.success(conversations)
        } catch (e: Exception) {
            Log.e("MessageRepo", "getRecentConversations failed", e)
            Result.failure(e)
        }
    }

    override suspend fun getConversation(
        user1Id: String,
        user2Id: String,
        page: Int,
        size: Int
    ): Result<List<Message>> {
        return try {
            val response = api.getConversation(user1Id, user2Id, page, size)
            Result.success(response.content.map { it.toMessage() })
        } catch (e: Exception) {
            Log.e("MessageRepo", "getConversation failed", e)
            Result.failure(e)
        }
    }

    override suspend fun sendMessage(
        senderId: String,
        receiverId: String,
        content: String
    ): Result<Message> {
        return try {
            val response = api.sendMessage(
                SendMessageRequest(
                    senderId = senderId,
                    receiverId = receiverId,
                    content = content
                )
            )
            Result.success(response.toMessage())
        } catch (e: Exception) {
            Log.e("MessageRepo", "sendMessage failed", e)
            Result.failure(e)
        }
    }

    override suspend fun markAsRead(messageId: String): Result<Unit> {
        return try {
            val response = api.markAsRead(messageId)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("markAsRead failed: HTTP ${response.code()}"))
        } catch (e: Exception) {
            Log.e("MessageRepo", "markAsRead failed", e)
            Result.failure(e)
        }
    }

    private fun MessageDto.toMessage() = Message(
        id = id,
        senderId = senderId,
        senderName = senderName,
        receiverId = receiverId,
        receiverName = receiverName,
        content = content,
        attachmentUrl = attachmentUrl,
        sentAt = sentAt,
        isRead = isRead
    )
}