package com.example.Roomdb.domain.usecases.employer

import com.example.Roomdb.data.model.Worker
import com.example.Roomdb.domain.repository.employer.WorkerRepository

class GetWorkersUseCase(
    private val repository: WorkerRepository
) {
    suspend operator fun invoke(location: String? = null, forceRefresh: Boolean = false): Result<List<Worker>> {
        return repository.getWorkers(location, forceRefresh)
    }
}