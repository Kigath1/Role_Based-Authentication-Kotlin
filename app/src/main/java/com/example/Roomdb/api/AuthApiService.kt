package com.example.Roomdb.api

import com.example.Roomdb.data.remote.model.auth.LoginRequest
import com.example.Roomdb.data.remote.model.auth.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")   // adjust to your actual endpoint path
    suspend fun login(@Body request: LoginRequest): LoginResponse

}