package com.example.Roomdb.data.remote.model.employer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkerMarktetplaceModels(
    @SerialName("id") val id: String,
    @SerialName("userId") val userId: String,
    @SerialName("username") val username: String,
    @SerialName("email") val email: String,
    @SerialName("fullName") val fullName: String,
    @SerialName("profilePictureUrl") val profilePictureUrl: String? = null,
    @SerialName("category") val category: String,
    @SerialName("location") val location: String,
    @SerialName("hourlyRate") val hourlyRate: Double,
    @SerialName("experienceYears") val experienceYears: Int,
    @SerialName("isOnline") val isOnline: Boolean,
    @SerialName("bio") val bio: String,
    @SerialName("skills") val skills: List<String>,
    @SerialName("preferredLocations") val preferredLocations: List<String>,
    @SerialName("averageRating") val averageRating: Double,
    @SerialName("reviewCount") val reviewCount: Int,
    @SerialName("status") val status: String
)

// The API returns a plain list — wrap it for flexibility

@Serializable
data class WorkerSearchResponse(
    val content: List<WorkerMarktetplaceModels>,
    val pageable: Pageable,
    val totalPages: Int,
    val totalElements: Int,
    val last: Boolean,
    val numberOfElements: Int,
    val size: Int,
    val number: Int,
    val sort: Sort,
    val first: Boolean,
    val empty: Boolean
)

@Serializable
data class Pageable(
    val pageNumber: Int,
    val pageSize: Int,
    val sort: Sort,
    val offset: Int,
    val unpaged: Boolean,
    val paged: Boolean
)

@Serializable
data class Sort(
    val unsorted: Boolean,
    val sorted: Boolean,
    val empty: Boolean
)