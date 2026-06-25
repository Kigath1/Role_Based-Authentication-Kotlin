package com.example.Roomdb.domain.usecases.employer

import com.example.Roomdb.data.model.employer.Message
import com.example.Roomdb.data.model.employer.RecentConversation
import com.example.Roomdb.domain.repository.employer.MessageRepository


/**
 * Retrieves the list of recent conversations for a given user.
 */
class GetRecentConversationsUseCase(private val repository: MessageRepository) {
    suspend operator fun invoke(userId: String): Result<List<RecentConversation>> =
        repository.getRecentConversations(userId)
}

/**
 * Retrieves a paginated conversation between two users.
 */
class GetConversationUseCase(private val repository: MessageRepository) {
    suspend operator fun invoke(
        user1Id: String,
        user2Id: String,
        page: Int = 0,
        size: Int = 50
    ): Result<List<Message>> =
        repository.getConversation(user1Id, user2Id, page, size)
}

/**
 * Sends a message from one user to another.
 */
class SendMessageUseCase(private val repository: MessageRepository) {
    suspend operator fun invoke(
        senderId: String,
        receiverId: String,
        content: String
    ): Result<Message> =
        repository.sendMessage(senderId, receiverId, content)
}