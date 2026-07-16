package com.example.Roomdb.data.remote.model

import kotlinx.serialization.Serializable

// ── STK Push ──────────────────────────────────────────────
// NOTE: PaymentStatusResponse is already defined in JobDto.kt (worker side) —
// reused here as-is, not redeclared.
@Serializable
data class StkPushRequest(
    val jobId: String,
    val phoneNumber: String
)

@Serializable
data class StkPushResponse(
    val status: String,              // "PENDING"
    val checkoutRequestId: String?,
    val message: String?,
    val merchantRequestID: String?
)

// ── Escrow Release / Refund ──────────────────────────────
@Serializable
data class EscrowActionResponse(
    val status: String,              // "RELEASED" or "REFUNDED"
    val message: String?
)

// ── Payment Receipt ───────────────────────────────────────
@Serializable
data class PaymentReceiptResponse(
    val mpesaReceiptNumber: String?,
    val transactionDate: String?,
    val amount: Double?,
    val platformFee: Double?,
    val workerAmount: Double?,
    val status: String?
)

// ── Reviews ───────────────────────────────────────────────
@Serializable
data class CreateReviewRequest(
    val rating: Int,
    val comment: String
)

@Serializable
data class ReviewDto(
    val id: String,
    val rating: Int,
    val comment: String?,
    val client: JobPartyDto?,
    val worker: JobPartyDto?,
    val createdAt: String?
)