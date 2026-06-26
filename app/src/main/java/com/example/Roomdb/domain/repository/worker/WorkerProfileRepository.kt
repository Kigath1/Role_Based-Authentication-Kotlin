package com.example.Roomdb.domain.repository.worker

import com.example.Roomdb.data.remote.model.WorkerModels


interface WorkerProfileRepository {
    suspend fun createProfile(
        email: String,
        request: WorkerModels.WorkerProfileRequest
    ): Result<WorkerModels.WorkerProfileResponse>

    suspend fun getProfile(userId: String): Result<WorkerModels.WorkerProfileResponse>

    suspend fun updateProfile(
        userId: String,
        request: WorkerModels.WorkerProfileRequest
    ): Result<WorkerModels.WorkerProfileResponse>

    suspend fun uploadDocument(
        userId: String, type: String, name: String,
        fileBytes: ByteArray, mimeType: String
    ): Result<WorkerModels.DocumentResponse>

    suspend fun getDocuments(userId: String): Result<List<WorkerModels.DocumentResponse>>
}