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

// Response shape is inconsistent across endpoints (create/accept/reject each omit
// different fields) — everything nullable except id/description/status.
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
    val worker: JobPartyDto? = null
)