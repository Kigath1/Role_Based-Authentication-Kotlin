package com.example.Roomdb.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class JobRequestBody(
    val description: String,
    val location: String,
    val scheduledDate: String,
    val jobPrice: Double,
    val totalCost: Double
)
@Serializable
data class JobRequestResponse(
    val id: String,
    val description: String,
    val status: String,
    val totalCost: Long,
    val client: Client,
    val worker: Worker,
)
@Serializable
data class Client(
    val id: String,
    val fullName: String,
)
@Serializable
data class Worker(
    val id: String,
    val fullName: String,
)


@Serializable
data class JobPartyDto(
    val id: String? = null,
    val fullName: String? = null
)

@Serializable
data class JobDto(
    val id: String,
    val description: String,
    val status: String,
    val location: String? = null,
    val scheduledDate: String? = null,
    val jobPrice: Double? = null,
    val totalCost: Double? = null,
    val negotiatedPrice: Double? = null,
    val rejectionReason: String? = null,
    val cancellationReason: String? = null,
    val client: JobPartyDto? = null,
    val worker: JobPartyDto? = null,
    val escrowAmount: Double? = null,
    val paymentId: String? = null,
    val paymentStatus: String? = null,
    val paymentAmount: Double? = null,
    val platformFee: Double? = null,
    val workerNetAmount: Double? = null,
    val escrowFunded: Boolean? = null,
    val escrowMessage: String? = null,
    val mpesaReceiptNumber: String? = null
)

@Serializable
data class PaymentStatusResponse(
    val status: String,
    val id: String?,
    val jobId: String,
    val amount: Double?,
    val phoneNumber: String?,
    val checkoutRequestId: String?,
    val mpesaReceiptNumber: String?,
    val platformFee: Double?,
    val workerAmount: Double?,
    val message: String?,
    val transactionDate: String?,
    val createdAt: String?,
    val timeoutAt: String?,
    val failureReason: String?
)