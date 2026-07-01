package com.example.Roomdb.api.worker

import com.example.Roomdb.data.remote.model.WorkerModels
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface WorkerProfileApiService {

    @POST("workers/profile")
    suspend fun createProfile(
        @Query("email") email: String,
        @Body request: WorkerModels.WorkerProfileRequest
    ): WorkerModels.WorkerProfileResponseWrapper

    @GET("/api/workers/profile/{userId}")
    suspend fun getProfile(@Path("userId") userId: String): Response<WorkerModels.WorkerProfileResponse>

    @PUT("workers/profile/{userId}")
    suspend fun updateProfile(
        @Path("userId") userId: String,
        @Body request: WorkerModels.WorkerProfileRequest
    ): WorkerModels.WorkerProfileResponseWrapper

    @Multipart
    @POST("documents")
    suspend fun uploadDocument(
        @Query("userId") userId: String,
        @Query("type") type: String,
        @Query("name") name: String,
        @Part file: MultipartBody.Part
    ): WorkerModels.DocumentResponse

    @GET("documents/worker/user/{userId}")
    suspend fun getDocuments(
        @Path("userId") userId: String
    ): List<WorkerModels.DocumentResponse>
}