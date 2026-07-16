package com.example.Roomdb.data.repositoryimpl.employer

import com.example.Roomdb.api.employer.ClientJobApiService
import com.example.Roomdb.data.model.Job
import com.example.Roomdb.data.model.PaymentStatus
import com.example.Roomdb.data.remote.model.CreateReviewRequest
import com.example.Roomdb.data.remote.model.JobRequestBody
import com.example.Roomdb.data.remote.model.PaymentReceiptResponse
import com.example.Roomdb.data.remote.model.ReviewDto
import com.example.Roomdb.data.remote.model.StkPushRequest
import com.example.Roomdb.data.remote.model.StkPushResponse
import com.example.Roomdb.domain.repository.employer.ClientJobRepository

class ClientJobRepositoryImpl(
    private val api: ClientJobApiService
) : ClientJobRepository {

    // ── Existing (unchanged) ─────────────────────────────

    override suspend fun createJobRequest(
        clientId: String, workerUserId: String,
        description: String, location: String, scheduledDate: String, budget: Double
    ): Result<Job> = runCatching {
        Job.fromDto(
            api.createJobRequest(
                clientId, workerUserId,
                JobRequestBody(description, location, scheduledDate, budget, budget)
            )
        )
    }.recoverCatching { throw Exception(it.message) }

    override suspend fun acceptCounterOffer(jobId: String): Result<Job> = runCatching {
        Job.fromDto(api.acceptCounterOffer(jobId))
    }.recoverCatching { throw Exception(it.message) }

    override suspend fun cancelJob(jobId: String): Result<Job> = runCatching {
        Job.fromDto(api.cancelJob(jobId))
    }.recoverCatching { throw Exception(it.message) }

    override suspend fun getClientJobs(clientId: String): Result<List<Job>> = runCatching {
        api.getClientJobs(clientId).map { Job.fromDto(it) }
    }.recoverCatching { throw Exception(it.message) }

    // ── New: Escrow / Payments ───────────────────────────

    override suspend fun fundEscrow(jobId: String, phoneNumber: String): Result<StkPushResponse> = runCatching {
        api.initiateStkPush(StkPushRequest(jobId = jobId, phoneNumber = phoneNumber))
    }.recoverCatching { throw Exception(it.message) }

    override suspend fun checkPaymentStatus(jobId: String): Result<PaymentStatus> = runCatching {
        PaymentStatus.fromApi(api.getPaymentStatus(jobId).status)
    }.recoverCatching { throw Exception(it.message) }

    override suspend fun getPaymentReceipt(jobId: String): Result<PaymentReceiptResponse> = runCatching {
        api.getPaymentReceipt(jobId)
    }.recoverCatching { throw Exception(it.message) }

    override suspend fun releaseEscrow(jobId: String): Result<String> = runCatching {
        api.releaseEscrow(jobId).message ?: "Escrow released to worker wallet."
    }.recoverCatching { throw Exception(it.message) }

    override suspend fun refundEscrow(jobId: String): Result<String> = runCatching {
        api.refundEscrow(jobId).message ?: "Escrow refunded to client wallet."
    }.recoverCatching { throw Exception(it.message) }

    // ── New: Reviews ──────────────────────────────────────

    override suspend fun createReview(
        clientId: String,
        workerProfileId: String,
        jobId: String?,
        rating: Int,
        comment: String
    ): Result<ReviewDto> = runCatching {
        api.createReview(
            clientId = clientId,
            workerProfileId = workerProfileId,
            jobId = jobId,
            body = CreateReviewRequest(rating = rating, comment = comment)
        )
    }.recoverCatching { throw Exception(it.message) }
}