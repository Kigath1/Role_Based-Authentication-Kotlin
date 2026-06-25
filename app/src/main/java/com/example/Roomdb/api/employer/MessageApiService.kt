package com.example.Roomdb.api.employer

import com.example.Roomdb.data.remote.model.employer.ConversationResponse
import com.example.Roomdb.data.remote.model.employer.MessageDto
import com.example.Roomdb.data.remote.model.employer.SendMessageRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MessageApiService {
    @GET("messages/user/{userId}/recent")
    suspend fun getRecentConversations(
        @Path("userId") userId: String
    ): List<MessageDto>

    @GET("messages/conversation")
    suspend fun getConversation(
        @Query("user1Id") user1Id: String,
        @Query("user2Id") user2Id: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): ConversationResponse

    @POST("messages")
    suspend fun sendMessage(
        @Body request: SendMessageRequest
    ): MessageDto
}