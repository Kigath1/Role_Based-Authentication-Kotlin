package com.example.Roomdb.domain.repository.worker

import com.example.Roomdb.data.model.Job
import com.example.Roomdb.data.model.PaymentStatus

interface WorkerJobRepository {
    suspend fun acceptJob(jobId: String): Result<Job>
    suspend fun rejectJob(jobId: String): Result<Job>
    suspend fun counterOffer(jobId: String, counterPrice: Double): Result<Job>
    suspend fun startJob(jobId: String): Result<Job>
    suspend fun completeJob(jobId: String): Result<Job>
    suspend fun getWorkerJobs(workerUserId: String): Result<List<Job>>

    suspend fun checkPaymentStatus(jobId: String): Result<PaymentStatus>
}