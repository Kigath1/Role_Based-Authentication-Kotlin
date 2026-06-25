package com.example.Roomdb.domain.repository.employer

import com.example.Roomdb.data.model.employer.Message
import com.example.Roomdb.data.model.employer.RecentConversation


interface MessageRepository {
    suspend fun getRecentConversations(userId: String): Result<List<RecentConversation>>
    suspend fun getConversation(user1Id: String, user2Id: String, page: Int, size: Int): Result<List<Message>>
    suspend fun sendMessage(senderId: String, receiverId: String, content: String): Result<Message>
}