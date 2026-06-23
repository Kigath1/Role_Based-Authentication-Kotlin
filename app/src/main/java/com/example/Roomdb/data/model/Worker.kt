package com.example.Roomdb.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Worker(
    val id: String,
    val userId: String,
    val username: String,
    val email: String,
    val fullName: String,
    val profilePictureUrl: String?,
    val category: String,
    val location: String,
    val hourlyRate: Double,
    val experienceYears: Int,
    val isOnline: Boolean,
    val bio: String,
    val skills: List<String>,
    val preferredLocations: List<String>,
    val averageRating: Double,
    val reviewCount: Int,
    val status: String
)