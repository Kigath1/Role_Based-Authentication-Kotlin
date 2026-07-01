package com.example.Roomdb.domain.usecases.worker

import com.example.Roomdb.data.remote.model.WorkerModels
import com.example.Roomdb.domain.repository.worker.WorkerProfileRepository


class CreateWorkerProfileUseCase(private val repo: WorkerProfileRepository) {
    suspend operator fun invoke(email: String, request: WorkerModels.WorkerProfileRequest) =
        repo.createProfile(email, request)
}

class UpdateWorkerProfileUseCase(private val repo: WorkerProfileRepository) {
    suspend operator fun invoke(userId: String, request: WorkerModels.WorkerProfileRequest) =
        repo.updateProfile(userId, request)
}

class UploadDocumentUseCase(private val repo: WorkerProfileRepository) {
    suspend operator fun invoke(
        userId: String, type: String, name: String,
        fileBytes: ByteArray, mimeType: String
    ) = repo.uploadDocument(userId, type, name, fileBytes, mimeType)
}

class CheckWorkerProfileExistsUseCase(
    private val repository: WorkerProfileRepository
) {
    suspend operator fun invoke(userId: String): Result<Boolean> {
        return repository.getProfile(userId).map { it != null }
    }
}