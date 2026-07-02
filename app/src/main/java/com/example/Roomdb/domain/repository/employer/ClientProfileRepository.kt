package com.example.Roomdb.domain.repository.employer

import com.example.Roomdb.data.remote.model.employer.ClientModels

interface ClientProfileRepository {
    suspend fun createProfile(
        email: String,
        fullName: String,
        phoneNumber: String,
        location: String
    ): Result<ClientModels.ClientProfileResponse>

    suspend fun getProfile(userId: String): Result<ClientModels.ClientProfileResponse?>

    suspend fun updateProfile(
        userId: String,
        fullName: String,
        phoneNumber: String,
        location: String
    ): Result<ClientModels.ClientProfileResponse>
}