package com.example.Roomdb.domain.usecases.employer

import com.example.Roomdb.data.remote.model.PaymentReceiptResponse
import com.example.Roomdb.data.remote.model.ReviewDto
import com.example.Roomdb.data.remote.model.StkPushResponse
import com.example.Roomdb.domain.repository.employer.ClientJobRepository

class FundEscrowUseCase(private val repo: ClientJobRepository) {
    suspend operator fun invoke(jobId: String, phoneNumber: String): Result<StkPushResponse> =
        repo.fundEscrow(jobId, phoneNumber)
}

class CheckClientPaymentStatusUseCase(private val repo: ClientJobRepository) {
    suspend operator fun invoke(jobId: String) = repo.checkPaymentStatus(jobId)
}

class GetPaymentReceiptUseCase(private val repo: ClientJobRepository) {
    suspend operator fun invoke(jobId: String): Result<PaymentReceiptResponse> = repo.getPaymentReceipt(jobId)
}

class ReleaseEscrowUseCase(private val repo: ClientJobRepository) {
    suspend operator fun invoke(jobId: String): Result<String> = repo.releaseEscrow(jobId)
}

class RefundEscrowUseCase(private val repo: ClientJobRepository) {
    suspend operator fun invoke(jobId: String): Result<String> = repo.refundEscrow(jobId)
}

class SubmitReviewUseCase(private val repo: ClientJobRepository) {
    suspend operator fun invoke(
        clientId: String, workerProfileId: String, jobId: String?, rating: Int, comment: String
    ): Result<ReviewDto> = repo.createReview(clientId, workerProfileId, jobId, rating, comment)
}