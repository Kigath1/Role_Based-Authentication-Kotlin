package com.example.Roomdb.api

import com.example.Roomdb.data.remote.model.employer.WorkerSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WorkerApiService {
    @GET("marketplace/search")
    suspend fun getWorkers(
        @Query("location") location: String? = null
    ): WorkerSearchResponse

}