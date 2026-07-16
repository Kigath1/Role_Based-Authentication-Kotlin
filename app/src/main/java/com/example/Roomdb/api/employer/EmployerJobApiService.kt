package com.example.Roomdb.api.employer

import com.example.Roomdb.data.remote.model.CreateReviewRequest
import com.example.Roomdb.data.remote.model.EscrowActionResponse
import com.example.Roomdb.data.remote.model.JobDto
import com.example.Roomdb.data.remote.model.JobRequestBody
import com.example.Roomdb.data.remote.model.PaymentReceiptResponse
import com.example.Roomdb.data.remote.model.PaymentStatusResponse
import com.example.Roomdb.data.remote.model.ReviewDto
import com.example.Roomdb.data.remote.model.StkPushRequest
import com.example.Roomdb.data.remote.model.StkPushResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ClientJobApiService {

    // ── Existing (unchanged) ─────────────────────────────

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

    // ── New: Escrow / Payments ───────────────────────────

    @POST("payments/mpesa/stkpush")
    suspend fun initiateStkPush(@Body body: StkPushRequest): StkPushResponse

    @GET("payments/status/{jobId}")
    suspend fun getPaymentStatus(@Path("jobId") jobId: String): PaymentStatusResponse

    @GET("payments/receipt/{jobId}")
    suspend fun getPaymentReceipt(@Path("jobId") jobId: String): PaymentReceiptResponse

    @POST("payments/escrow/release/{jobId}")
    suspend fun releaseEscrow(@Path("jobId") jobId: String): EscrowActionResponse

    @POST("payments/escrow/refund/{jobId}")
    suspend fun refundEscrow(@Path("jobId") jobId: String): EscrowActionResponse

    // ── New: Reviews ──────────────────────────────────────

    @POST("reviews")
    suspend fun createReview(
        @Query("clientId") clientId: String,
        @Query("workerProfileId") workerProfileId: String,
        @Query("jobId") jobId: String?,
        @Body body: CreateReviewRequest
    ): ReviewDto
}