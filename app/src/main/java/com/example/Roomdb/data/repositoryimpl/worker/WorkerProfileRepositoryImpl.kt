package com.example.Roomdb.data.repositoryimpl.worker


import com.example.Roomdb.api.worker.WorkerProfileApiService
import com.example.Roomdb.data.remote.model.worker.WorkerModels
import com.example.Roomdb.domain.repository.worker.WorkerProfileRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class WorkerProfileRepositoryImpl(
    private val api: WorkerProfileApiService
) : WorkerProfileRepository {

    override suspend fun createProfile(
        email: String, request: WorkerModels.WorkerProfileRequest
    ): Result<WorkerModels.WorkerProfileResponse> = runCatching {
        api.createProfile(email, request).profile
    }

    override suspend fun updateProfile(
        userId: String, request: WorkerModels.WorkerProfileRequest
    ): Result<WorkerModels.WorkerProfileResponse> = runCatching {
        api.updateProfile(userId, request).profile
    }

    override suspend fun uploadDocument(
        userId: String, type: String, name: String,
        fileBytes: ByteArray, mimeType: String
    ): Result<WorkerModels.DocumentResponse> = runCatching {
        val requestBody = fileBytes.toRequestBody(mimeType.toMediaType())
        val part = MultipartBody.Part.createFormData("file", "upload_$type", requestBody)
        api.uploadDocument(userId, type, name, part)
    }

    override suspend fun getDocuments(
        userId: String
    ): Result<List<WorkerModels.DocumentResponse>> = runCatching {
        api.getDocuments(userId)
    }

    override suspend fun getProfile(userId: String): Result<WorkerModels.WorkerProfileResponse?> {
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

}