package com.example.Roomdb.data.repositoryimpl.employer

import com.example.Roomdb.api.employer.ClientProfileApiService
import com.example.Roomdb.data.remote.model.employer.ClientModels
import com.example.Roomdb.domain.repository.employer.ClientProfileRepository


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
    ): Result<ClientModels.ClientProfileResponse> = runCatching {
        api.getProfile(userId)
    }
}