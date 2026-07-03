package com.example.Roomdb.domain.repository.employer

import com.example.Roomdb.data.model.Job

interface ClientJobRepository {
    suspend fun createJobRequest(
        clientId: String, workerUserId: String,
        description: String, location: String, scheduledDate: String, budget: Double
    ): Result<Job>

    suspend fun acceptCounterOffer(jobId: String): Result<Job>
    suspend fun cancelJob(jobId: String): Result<Job>
    suspend fun getClientJobs(clientId: String): Result<List<Job>>
}