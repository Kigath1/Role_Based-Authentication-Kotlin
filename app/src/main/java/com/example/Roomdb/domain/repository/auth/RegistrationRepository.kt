package com.example.Roomdb.domain.repository.auth

interface RegistrationRepository {
    suspend fun register(
        username: String, email: String, password: String,
        firstName: String, secondName: String, role: String
    ): Result<Unit>

    suspend fun verifyEmail(token: String, email: String?): Result<Unit>
    suspend fun resendVerification(email: String): Result<Unit>
}