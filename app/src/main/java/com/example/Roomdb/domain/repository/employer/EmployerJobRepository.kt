package com.example.Roomdb.domain.repository.employer

import com.example.Roomdb.data.model.Job
import com.example.Roomdb.data.model.PaymentStatus
import com.example.Roomdb.data.remote.model.PaymentReceiptResponse
import com.example.Roomdb.data.remote.model.ReviewDto
import com.example.Roomdb.data.remote.model.StkPushResponse

interface ClientJobRepository {
    // ── Existing ──────────────────────────────────────────
    suspend fun createJobRequest(
        clientId: String, workerUserId: String,
        description: String, location: String, scheduledDate: String, budget: Double
    ): Result<Job>

    suspend fun acceptCounterOffer(jobId: String): Result<Job>
    suspend fun cancelJob(jobId: String): Result<Job>
    suspend fun getClientJobs(clientId: String): Result<List<Job>>

    // ── New: Escrow / Payments ───────────────────────────
    suspend fun fundEscrow(jobId: String, phoneNumber: String): Result<StkPushResponse>
    suspend fun checkPaymentStatus(jobId: String): Result<PaymentStatus>
    suspend fun getPaymentReceipt(jobId: String): Result<PaymentReceiptResponse>
    suspend fun releaseEscrow(jobId: String): Result<String>
    suspend fun refundEscrow(jobId: String): Result<String>

    // ── New: Reviews ──────────────────────────────────────
    suspend fun createReview(
        clientId: String,
        workerProfileId: String,
        jobId: String?,
        rating: Int,
        comment: String
    ): Result<ReviewDto>
}