package com.example.Roomdb.api.employer

import com.example.Roomdb.data.remote.model.employer.ClientModels
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ClientProfileApiService {

    @POST("clients/profile")
    suspend fun createProfile(
        @Query("email") email: String,
        @Body request: ClientModels.ClientProfileRequest
    ): ClientModels.ClientProfileResponse

    @GET("clients/profile/{userId}")
    suspend fun getProfile(
        @Path("userId") userId: String
    ): Response<ClientModels.ClientProfileResponse>

    @PUT("clients/profile/user/{userId}")
    suspend fun updateProfile(
        @Path("userId") userId: String,
        @Body request: ClientModels.ClientProfileRequest
    ): ClientModels.ClientProfileResponse
}