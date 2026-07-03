package com.example.Roomdb.api.employer

import com.example.Roomdb.data.remote.model.JobDto
import com.example.Roomdb.data.remote.model.JobRequestBody
import com.example.Roomdb.data.remote.model.employer.JobRequestResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.Query

interface ClientJobApiService {

    @POST("jobs/request")
    suspend fun createJobRequest(
        @Query("clientId") clientId: String,
        @Query("workerUserId") workerUserId: String,
        @Body body: JobRequestBody
    ): JobDto

    @POST("jobs/{jobId}/accept-counter-offer")
    suspend fun acceptCounterOffer(@Path("jobId") jobId: String): JobDto

    @POST("jobs/{jobId}/cancel")
    suspend fun cancelJob(@Path("jobId") jobId: String): JobDto

    @GET("jobs/client/{clientId}")
    suspend fun getClientJobs(@Path("clientId") clientId: String): List<JobDto>
}