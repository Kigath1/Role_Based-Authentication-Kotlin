package com.example.Roomdb.api.worker

import com.example.Roomdb.data.model.JobStatus
import com.example.Roomdb.data.remote.model.JobDto
import com.example.Roomdb.data.remote.model.PaymentStatusResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface WorkerJobApiService {

    @PUT("jobs/{jobId}/status")
    suspend fun acceptJob(
        @Path("jobId") jobId: String,
        @Query("status") status: JobStatus = JobStatus.ACCEPTED
    ): JobDto

    @PUT("jobs/{jobId}/status")
    suspend fun rejectJob(
        @Path("jobId") jobId: String,
        @Query("status") status: JobStatus = JobStatus.REJECTED
    ): JobDto

    @POST("jobs/{jobId}/counter-offer")
    suspend fun counterOffer(
        @Path("jobId") jobId: String,
        @Query("counterPrice") counterPrice: Double
    ): JobDto

    @PUT("jobs/{jobId}/status")
    suspend fun startJob(
        @Path("jobId") jobId: String,
        @Query("status") status: JobStatus = JobStatus.IN_PROGRESS
    ): JobDto

    @PUT("jobs/{jobId}/status")
    suspend fun completeJob(
        @Path("jobId") jobId: String,
        @Query("status") status: JobStatus = JobStatus.COMPLETED
    ): JobDto

    @GET("jobs/worker/user/{workerUserId}")
    suspend fun getWorkerJobs(@Path("workerUserId") workerUserId: String): List<JobDto>

    @GET("payments/status/{jobId}")
    suspend fun getPaymentStatus(@Path("jobId") jobId: String): PaymentStatusResponse
}