package com.example.Roomdb.data.repository

import android.util.Log
import com.example.Roomdb.api.WorkerApiService
import com.example.Roomdb.data.local.dao.WorkerDao
import com.example.Roomdb.data.local.entities.WorkerEntity
import com.example.Roomdb.data.model.Worker
import com.example.Roomdb.domain.repository.employer.WorkerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class WorkerRepositoryImpl(
    private val api: WorkerApiService,
    private val dao: WorkerDao
) : WorkerRepository {

    override suspend fun getWorkers(location: String?, forceRefresh: Boolean): Result<List<Worker>> {
        return withContext(Dispatchers.IO) {

            // --- 1. Try network first ---
            val response = try {
                api.getWorkers(location)
            } catch (networkEx: Exception) {
                // Genuine network failure — fall back to cache
                Log.w("WorkerRepo", "Network failed, trying cache", networkEx)
                val cached = dao.getAllWorkers()
                return@withContext if (cached.isNotEmpty()) {
                    Result.success(cached.map { it.toWorker() })
                } else {
                    Result.failure(networkEx)
                }
            }

            // --- 2. Network succeeded — map the response ---
            val workers = response.content.map { dto ->
                Worker(
                    id = dto.id,
                    userId = dto.userId,
                    username = dto.username,
                    email = dto.email,
                    fullName = dto.fullName,
                    profilePictureUrl = dto.profilePictureUrl,
                    category = dto.category,
                    location = dto.location,
                    hourlyRate = dto.hourlyRate,
                    experienceYears = dto.experienceYears,
                    isOnline = dto.isOnline,
                    bio = dto.bio,
                    skills = dto.skills,
                    preferredLocations = dto.preferredLocations,
                    averageRating = dto.averageRating,
                    reviewCount = dto.reviewCount,
                    status = dto.status
                )
            }

            // --- 3. Write to cache — isolated so a DB error never hides fresh data ---
            try {
                dao.clearAll()
                dao.insertAll(workers.map { it.toEntity() })
            } catch (dbEx: Exception) {
                Log.w("WorkerRepo", "Cache write failed — returning fresh network data anyway", dbEx)
            }

            Result.success(workers)
        }
    }

    // --- Extension helpers keep the mapping logic out of the main function ---

    private fun WorkerEntity.toWorker() = Worker(
        id = id,
        userId = userId,
        username = username,
        email = email,
        fullName = fullName,
        profilePictureUrl = profilePictureUrl,
        category = category,
        location = location,
        hourlyRate = hourlyRate,
        experienceYears = experienceYears,
        isOnline = isOnline,
        bio = bio,
        skills = skills,
        preferredLocations = preferredLocations,
        averageRating = averageRating,
        reviewCount  = reviewCount,
        status = status
    )

    private fun Worker.toEntity() = WorkerEntity(
        id = id,
        userId = userId,
        username = username,
        email = email,
        fullName = fullName,
        profilePictureUrl = profilePictureUrl,
        category = category,
        location = location,
        hourlyRate = hourlyRate,
        experienceYears = experienceYears,
        isOnline = isOnline,
        bio = bio,
        skills = skills,
        preferredLocations = preferredLocations,
        averageRating = averageRating,
        reviewCount  = reviewCount,
        status = status
    )
}