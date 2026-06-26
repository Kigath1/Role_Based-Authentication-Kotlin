package com.example.Roomdb.data.repositoryimpl.auth

import com.example.Roomdb.api.auth.AuthApiService
import com.example.Roomdb.data.remote.model.auth.RegistrationModels
import com.example.Roomdb.domain.repository.auth.RegistrationRepository
import retrofit2.HttpException
import java.io.IOException

class RegistrationRepositoryImpl(
    private val api: AuthApiService
) : RegistrationRepository {

    override suspend fun register(
        username: String, email: String, password: String,
        firstName: String, secondName: String, role: String
    ): Result<Unit> = runCatching {
        val request = RegistrationModels.RegisterRequest(
            username, email, password, firstName, secondName, role
        )
        val response = api.register(request)
        if (!response.isSuccessful) {
            val errorMsg = when (response.code()) {
                400 -> "Invalid registration data"
                409 -> "User already exists"
                500 -> "Server error, please try again"
                else -> "Registration failed: ${response.code()}"
            }
            throw Exception(errorMsg)
        }
        val body = response.body()
        // The API returns success: true/false even with HTTP 200
        if (body == null || !body.success) {
            throw Exception(body?.message ?: "Registration failed")
        }
        // Success – we ignore the message
    }

    // verifyEmail and resendVerification stay the same (they return ApiResponse)
    override suspend fun verifyEmail(token: String, email: String?): Result<Unit> = runCatching {
        val response = api.verifyEmail(token, email)
        if (!response.success) throw Exception(response.message)
    }

    override suspend fun resendVerification(email: String): Result<Unit> = runCatching {
        val response = api.resendVerification(mapOf("email" to email))
        if (!response.success) throw Exception(response.message)
    }
}