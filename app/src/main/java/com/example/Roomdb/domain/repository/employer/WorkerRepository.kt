package com.example.Roomdb.domain.repository.employer

import com.example.Roomdb.data.model.Worker

interface WorkerRepository {
    suspend fun getWorkers(location: String? = null, forceRefresh: Boolean = false): Result<List<Worker>>
}