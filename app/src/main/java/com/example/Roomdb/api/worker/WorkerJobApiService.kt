package com.example.Roomdb.api.worker

import com.example.Roomdb.data.remote.model.JobDto
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface WorkerJobApiService {

    @POST("jobs/{jobId}/accept")
    suspend fun acceptJob(@Path("jobId") jobId: String): JobDto

    @POST("jobs/{jobId}/reject")
    suspend fun rejectJob(@Path("jobId") jobId: String): JobDto

    @POST("jobs/{jobId}/counter-offer")
    suspend fun counterOffer(
        @Path("jobId") jobId: String,
        @Query("counterPrice") counterPrice: Double
    ): JobDto

    @POST("jobs/{jobId}/start")
    suspend fun startJob(@Path("jobId") jobId: String): JobDto

    @POST("jobs/{jobId}/complete")
    suspend fun completeJob(@Path("jobId") jobId: String): JobDto

    @GET("jobs/worker/{workerUserId}")
    suspend fun getWorkerJobs(@Path("workerUserId") workerUserId: String): List<JobDto>
}