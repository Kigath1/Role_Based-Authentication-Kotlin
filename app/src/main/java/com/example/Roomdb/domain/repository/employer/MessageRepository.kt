package com.example.Roomdb.domain.repository.employer

import com.example.Roomdb.data.model.employer.Message
import com.example.Roomdb.data.model.employer.RecentConversation


interface MessageRepository {
    suspend fun getRecentConversations(userId: String): Result<List<RecentConversation>>

    suspend fun getConversation(
        user1Id: String,
        user2Id: String,
        page: Int = 0,
        size: Int = 50
    ): Result<List<Message>>

    suspend fun sendMessage(
        senderId: String,
        receiverId: String,
        content: String
    ): Result<Message>

    suspend fun markAsRead(messageId: String): Result<Unit>
}