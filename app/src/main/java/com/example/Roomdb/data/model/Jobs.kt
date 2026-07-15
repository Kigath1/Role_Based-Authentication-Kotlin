package com.example.Roomdb.data.model

import com.example.Roomdb.data.remote.model.JobDto


enum class JobStatus {
    PENDING, ACCEPTED, REJECTED, CANCELLED, IN_PROGRESS, COMPLETED, UNKNOWN, APPROVED, CLIENT_CANCELLED;

    companion object {
        fun fromApi(raw: String): JobStatus =
            entries.find { it.name == raw.uppercase() } ?: UNKNOWN
    }
}

data class JobRequest(
    val workerUserId: String,
    val description: String,
    val location: String,
    val scheduledDate: String,
    val estimatedBudget: Double
)

data class JobParty(
    val id: String,
    val fullName: String
)

enum class PaymentStatus {
    NO_PAYMENT,
    PAID,
    PENDING,
    WAITING,
    CONFIRMED,
    RELEASED,
    FAILED,
    REFUNDED,
    DISPUTED,
    UNKNOWN;

    companion object {
        fun fromApi(raw: String): PaymentStatus {
            return when (raw.uppercase()) {
                "NO_PAYMENT" -> NO_PAYMENT
                "PAID" -> PAID
                "PENDING" -> PENDING
                "WAITING" -> WAITING
                "CONFIRMED" -> CONFIRMED
                "RELEASED" -> RELEASED
                "FAILED" -> FAILED
                "REFUNDED" -> REFUNDED
                "DISPUTED" -> DISPUTED
                else -> UNKNOWN
            }
        }
    }
}

data class Job(
    val id: String,
    val description: String,
    val status: JobStatus,
    val location: String?,
    val scheduledDate: String?,
    val jobPrice: Double?,
    val totalCost: Double?,
    val negotiatedPrice: Double?,
    val rejectionReason: String?,
    val cancellationReason: String?,
    val client: JobParty?,
    val worker: JobParty?,
    val paymentStatus: PaymentStatus? = null,
    val paymentAmount: Double? = null,
    val platformFee: Double? = null,
    val workerNetAmount: Double? = null,
    val escrowFunded: Boolean? = null,
    val escrowMessage: String? = null,
    val mpesaReceiptNumber: String? = null
) {
    val isActive: Boolean
        get() = status in setOf(
            JobStatus.PENDING,
            JobStatus.ACCEPTED,
            JobStatus.IN_PROGRESS,
            JobStatus.APPROVED
        )

    val isWaitingForPayment: Boolean
        get() = status == JobStatus.ACCEPTED &&
                paymentStatus in setOf(PaymentStatus.NO_PAYMENT, PaymentStatus.PENDING)

    val isPaymentConfirmed: Boolean
        get() = paymentStatus == PaymentStatus.PAID || escrowFunded == true

    companion object {
        fun fromDto(dto: JobDto): Job {
            val status = when (dto.status.uppercase()) {
                "PENDING" -> JobStatus.PENDING
                "ACCEPTED" -> JobStatus.ACCEPTED
                "IN_PROGRESS" -> JobStatus.IN_PROGRESS
                "COMPLETED" -> JobStatus.COMPLETED
                "REJECTED" -> JobStatus.REJECTED
                "CANCELLED" -> JobStatus.CANCELLED
                "APPROVED" -> JobStatus.APPROVED
                "CLIENT_CANCELLED" -> JobStatus.CLIENT_CANCELLED
                else -> JobStatus.UNKNOWN
            }

            return Job(
                id = dto.id,
                description = dto.description,
                status = status,
                location = dto.location,
                scheduledDate = dto.scheduledDate,
                jobPrice = dto.jobPrice,
                totalCost = dto.totalCost,
                negotiatedPrice = dto.negotiatedPrice,
                rejectionReason = dto.rejectionReason,
                cancellationReason = dto.cancellationReason,
                client = dto.client?.takeIf { it.id != null && it.fullName != null }
                    ?.let { JobParty(it.id!!, it.fullName!!) },
                worker = dto.worker?.takeIf { it.id != null && it.fullName != null }
                    ?.let { JobParty(it.id!!, it.fullName!!) },
                paymentStatus = dto.paymentStatus?.let { PaymentStatus.fromApi(it) },
                paymentAmount = dto.paymentAmount,
                platformFee = dto.platformFee,
                workerNetAmount = dto.workerNetAmount,
                escrowFunded = dto.escrowFunded,
                escrowMessage = dto.escrowMessage,
                mpesaReceiptNumber = dto.mpesaReceiptNumber
            )
        }
    }
}