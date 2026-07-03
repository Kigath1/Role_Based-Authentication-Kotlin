package com.example.Roomdb.data.remote.model.worker

import kotlinx.serialization.Serializable

object WorkerModels {

    @Serializable
    data class WorkHistoryEntry(
        val company: String,
        val role: String,
        val period: String,
        val description: String
    )

    @Serializable
    data class Certification(
        val name: String,
        val issuer: String,
        val year: Int
    )

    @Serializable
    data class WorkerProfileRequest(
        val fullName: String,
        val phoneNumber: String,
        val bio: String? = null,
        val location: String,
        val experienceYears: Int,
        val hourlyRate: Double,
        val category: String,
        val profilePictureUrl: String? = null,
        val availabilityDetails: String? = null,
        val skills: List<String> = emptyList(),
        val preferredLocations: List<String> = emptyList(),
        val workHistory: List<WorkHistoryEntry> = emptyList(),
        val certifications: List<Certification> = emptyList()
    )

    // POST /workers/profile and PUT /workers/profile/{id} return { message, profile: {...} }
    @Serializable
    data class WorkerProfileResponseWrapper(
        val message: String,
        val profile: WorkerProfileResponse
    )

    // GET /workers/profile/{userId} returns the profile directly
    @Serializable
    data class WorkerProfileResponse(
        val id: String,
        val fullName: String,
        val email: String? = null,
        val phoneNumber: String,
        val bio: String? = null,
        val location: String? = null,
        val experienceYears: Int? = null,
        val hourlyRate: Double? = null,
        val category: String? = null,
        val profilePictureUrl: String? = null,
        val availabilityDetails: AvailabilityDetails? = null,
        val skills: List<String> = emptyList(),
        val preferredLocations: List<String> = emptyList(),
        val workHistory: List<WorkHistoryEntry> = emptyList(),
        val certifications: List<Certification> = emptyList(),
        val status: String? = null
    )

    @Serializable
    data class AvailabilityDetails(
        val weekdays: Boolean = false,
        val weekends: Boolean = false,
        val evenings: Boolean = false
    )

    @Serializable
    data class DocumentResponse(
        val id: String,
        val type: String,
        val name: String,
        val documentUrl: String,
        val verified: Boolean,
        val uploadedAt: String,
        val verifiedAt: String? = null
    )
}