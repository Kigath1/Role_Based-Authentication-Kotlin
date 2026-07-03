package com.example.Roomdb.data.remote.model.employer

import kotlinx.serialization.Serializable

@Serializable
data class JobRequest(
    val description: String,
    val location: String,
    val scheduledDate: String, // "yyyy-MM-dd"
    val jobPrice: Int,
    val totalCost: Int // send equal to jobPrice at creation
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

// All fields nullable except id/description/status – API response varies.
//@Serializable
data class EmployerJobResponses(
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

