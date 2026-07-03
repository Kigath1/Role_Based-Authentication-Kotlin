package com.example.Roomdb.domain.usecases.employer

import com.example.Roomdb.domain.repository.employer.ClientJobRepository

class CreateJobRequestUseCase(private val repo: ClientJobRepository) {
    suspend operator fun invoke(
        clientId: String, workerUserId: String,
        description: String, location: String, scheduledDate: String, budget: Double
    ) = repo.createJobRequest(clientId, workerUserId, description, location, scheduledDate, budget)
}

class AcceptCounterOfferUseCase(private val repo: ClientJobRepository) {
    suspend operator fun invoke(jobId: String) = repo.acceptCounterOffer(jobId)
}

class CancelJobUseCase(private val repo: ClientJobRepository) {
    suspend operator fun invoke(jobId: String) = repo.cancelJob(jobId)
}

class GetClientJobsUseCase(private val repo: ClientJobRepository) {
    suspend operator fun invoke(clientId: String) = repo.getClientJobs(clientId)
}