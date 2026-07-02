package com.example.Roomdb.data.repositoryimpl.employer

import com.example.Roomdb.api.employer.ClientProfileApiService
import com.example.Roomdb.data.remote.model.employer.ClientModels
import com.example.Roomdb.domain.repository.employer.ClientProfileRepository
import retrofit2.HttpException

class ClientProfileRepositoryImpl(
    private val api: ClientProfileApiService
) : ClientProfileRepository {

    override suspend fun createProfile(
        email: String, fullName: String, phoneNumber: String, location: String
    ): Result<ClientModels.ClientProfileResponse> = runCatching {
        api.createProfile(email, ClientModels.ClientProfileRequest(fullName, phoneNumber, location))
    }

    override suspend fun getProfile(
        userId: String
    ): Result<ClientModels.ClientProfileResponse?> {
        return try {
            val response = api.getProfile(userId)
            when {
                response.isSuccessful -> Result.success(response.body())
                response.code() == 404 -> Result.success(null) // no profile yet
                else -> Result.failure(Exception(HttpException(response)))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message))
        }
    }

    override suspend fun updateProfile(
        userId: String, fullName: String, phoneNumber: String, location: String
    ): Result<ClientModels.ClientProfileResponse> = runCatching {
        api.updateProfile(userId, ClientModels.ClientProfileRequest(fullName, phoneNumber, location))
    }
}