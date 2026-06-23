package com.example.Roomdb.domain.repository.auth

import com.example.Roomdb.data.model.UserProfile

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<UserProfile>
    suspend fun getCurrentUser(): UserProfile?
    suspend fun getUserRole(): String?
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}