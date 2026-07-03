package com.example.Roomdb.data.remote.model.worker

import kotlinx.serialization.Serializable

@Serializable
data class JobPartyDto(
    val id: String? = null,
    val fullName: String? = null
)

@Serializable
data class WorkerJobResponses(
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
    val worker: JobPartyDto? = null
)