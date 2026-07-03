package com.example.Roomdb.data.model

import com.example.Roomdb.data.remote.model.JobDto


enum class JobStatus {
    PENDING, ACCEPTED, REJECTED, CANCELLED, IN_PROGRESS, COMPLETED, UNKNOWN;

    companion object {
        fun fromApi(raw: String): JobStatus =
            entries.find { it.name == raw.uppercase() } ?: UNKNOWN
    }
}

data class JobRequest(
    val workerUserId: String, // the account-level ID — see the id-vs-userId gotcha
    val description: String,
    val location: String,
    val scheduledDate: String, // "yyyy-MM-dd"
    val estimatedBudget: Double
)

data class JobParty(
    val id: String,
    val fullName: String
)

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
    val worker: JobParty?
) {
    // Convenience for the Active/Completed split in both Jobs screens
    val isActive: Boolean
        get() = status in setOf(JobStatus.PENDING, JobStatus.ACCEPTED, JobStatus.IN_PROGRESS)

    companion object {
        fun fromDto(dto: JobDto) = Job(
            id = dto.id,
            description = dto.description,
            status = JobStatus.fromApi(dto.status),
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
                ?.let { JobParty(it.id!!, it.fullName!!) }
        )
    }
}