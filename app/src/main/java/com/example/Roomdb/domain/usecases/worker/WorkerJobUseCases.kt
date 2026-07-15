package com.example.Roomdb.domain.usecases.worker

import com.example.Roomdb.data.model.PaymentStatus
import com.example.Roomdb.domain.repository.worker.WorkerJobRepository

class AcceptJobUseCase(private val repo: WorkerJobRepository) {
    suspend operator fun invoke(jobId: String) = repo.acceptJob(jobId)
}

class RejectJobUseCase(private val repo: WorkerJobRepository) {
    suspend operator fun invoke(jobId: String) = repo.rejectJob(jobId)
}

class CounterOfferUseCase(private val repo: WorkerJobRepository) {
    suspend operator fun invoke(jobId: String, counterPrice: Double) = repo.counterOffer(jobId, counterPrice)
}

class StartJobUseCase(private val repo: WorkerJobRepository) {
    suspend operator fun invoke(jobId: String) = repo.startJob(jobId)
}

class CompleteJobUseCase(private val repo: WorkerJobRepository) {
    suspend operator fun invoke(jobId: String) = repo.completeJob(jobId)
}

class GetWorkerJobsUseCase(private val repo: WorkerJobRepository) {
    suspend operator fun invoke(workerUserId: String) = repo.getWorkerJobs(workerUserId)
}

class CheckPaymentStatusUseCase(
    private val repository: WorkerJobRepository
) {
    suspend operator fun invoke(jobId: String): Result<PaymentStatus> {
        return repository.checkPaymentStatus(jobId)
    }
}