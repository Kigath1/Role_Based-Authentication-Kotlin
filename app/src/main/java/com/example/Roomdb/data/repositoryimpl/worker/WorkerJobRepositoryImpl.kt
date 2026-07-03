package com.example.Roomdb.data.repositoryimpl.worker

import com.example.Roomdb.api.worker.WorkerJobApiService
import com.example.Roomdb.data.model.Job
import com.example.Roomdb.domain.repository.worker.WorkerJobRepository

class WorkerJobRepositoryImpl(
    private val api: WorkerJobApiService
) : WorkerJobRepository {

    override suspend fun acceptJob(jobId: String): Result<Job> = runCatching {
        Job.fromDto(api.acceptJob(jobId))
    }.recoverCatching { throw Exception(it.message) }

    override suspend fun rejectJob(jobId: String): Result<Job> = runCatching {
        Job.fromDto(api.rejectJob(jobId))
    }.recoverCatching { throw Exception(it.message) }

    override suspend fun counterOffer(jobId: String, counterPrice: Double): Result<Job> = runCatching {
        Job.fromDto(api.counterOffer(jobId, counterPrice))
    }.recoverCatching { throw Exception(it.message) }

    override suspend fun startJob(jobId: String): Result<Job> = runCatching {
        Job.fromDto(api.startJob(jobId))
    }.recoverCatching { throw Exception(it.message) }

    override suspend fun completeJob(jobId: String): Result<Job> = runCatching {
        Job.fromDto(api.completeJob(jobId))
    }.recoverCatching { throw Exception(it.message) }

    override suspend fun getWorkerJobs(workerUserId: String): Result<List<Job>> = runCatching {
        api.getWorkerJobs(workerUserId).map { Job.fromDto(it) }
    }.recoverCatching { throw Exception(it.message) }
}