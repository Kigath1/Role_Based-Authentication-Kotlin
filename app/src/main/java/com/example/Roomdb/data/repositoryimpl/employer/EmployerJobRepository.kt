package com.example.Roomdb.data.repositoryimpl.employer

import com.example.Roomdb.api.employer.ClientJobApiService
import com.example.Roomdb.data.model.Job
import com.example.Roomdb.data.remote.model.JobRequestBody
import com.example.Roomdb.domain.repository.employer.ClientJobRepository

class ClientJobRepositoryImpl(
    private val api: ClientJobApiService
) : ClientJobRepository {

    override suspend fun createJobRequest(
        clientId: String, workerUserId: String,
        description: String, location: String, scheduledDate: String, budget: Double
    ): Result<Job> = runCatching {
        Job.fromDto(
            api.createJobRequest(
                clientId, workerUserId,
                JobRequestBody(description, location, scheduledDate, budget, budget)
            )
        )
    }.recoverCatching { throw Exception(it.message) }

    override suspend fun acceptCounterOffer(jobId: String): Result<Job> = runCatching {
        Job.fromDto(api.acceptCounterOffer(jobId))
    }.recoverCatching { throw Exception(it.message) }

    override suspend fun cancelJob(jobId: String): Result<Job> = runCatching {
        Job.fromDto(api.cancelJob(jobId))
    }.recoverCatching { throw Exception(it.message) }

    override suspend fun getClientJobs(clientId: String): Result<List<Job>> = runCatching {
        api.getClientJobs(clientId).map { Job.fromDto(it) }
    }.recoverCatching { throw Exception(it.message) }
}