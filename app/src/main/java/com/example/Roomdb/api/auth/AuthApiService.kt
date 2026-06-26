package com.example.Roomdb.api.auth

import com.example.Roomdb.data.remote.model.auth.LoginRequest
import com.example.Roomdb.data.remote.model.auth.LoginResponse
import com.example.Roomdb.data.remote.model.auth.RegistrationModels
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {

    // ── EXISTING ──────────────────────────────────────────────────────────
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse   // assume LoginResponse is your existing model

    // ── NEW – now returns Response<String> ───────────────────────────────
    @POST("auth/register")
    suspend fun register(
        @Body request: RegistrationModels.RegisterRequest
    ): Response<RegistrationModels.RegisterResponse>

    @POST("auth/verify-email")
    suspend fun verifyEmail(
        @Query("token") token: String,
        @Query("email") email: String? = null
    ): RegistrationModels.ApiResponse   // if this endpoint returns JSON, keep as is

    @POST("auth/resend-verification")
    suspend fun resendVerification(
        @Body body: Map<String, String>
    ): RegistrationModels.ApiResponse
}