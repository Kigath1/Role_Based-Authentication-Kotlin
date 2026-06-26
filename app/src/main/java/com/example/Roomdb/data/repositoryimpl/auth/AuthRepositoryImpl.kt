package com.example.Roomdb.data.repositoryimpl.auth

import android.util.Log
import com.example.Roomdb.api.auth.AuthApiService
import com.example.Roomdb.data.local.SecureTokenDataStore
import com.example.Roomdb.data.local.dao.UserDao
import com.example.Roomdb.data.local.entities.UserProfileEntity
import com.example.Roomdb.data.model.UserProfile
import com.example.Roomdb.data.remote.model.auth.LoginRequest
import com.example.Roomdb.domain.repository.auth.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApiService,
    private val userDao: UserDao,
    private val secureStore: SecureTokenDataStore
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<UserProfile> {
        return try {
            val response = authApi.login(LoginRequest(email, password))
            if (!response.success) {
                return Result.failure(Exception(response.message))
            }
            val data = response.data

            // Secrets -> secure DataStore
            secureStore.saveSession(
                accessToken = data.accessToken,
                refreshToken = data.refreshToken,
                userId = data.userId
            )

            // Profile -> Room
            userDao.clearProfile()
            userDao.insertProfile(
                UserProfileEntity(
                    userId = data.userId,
                    username = data.username,
                    email = data.email,
                    name = data.name,
                    role = data.role,
                    profilePictureUrl = data.profilePictureUrl
                )
            )

            Result.success(
                UserProfile(
                    userId = data.userId,
                    username = data.username,
                    email = data.email,
                    name = data.name,
                    role = data.role,
                    profilePictureUrl = data.profilePictureUrl
                )
            )
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login failed", e)
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): UserProfile? {
        val profile = userDao.getProfile() ?: return null
        val userId = secureStore.getUserIdOnce() ?: return null
        return UserProfile(
            userId = userId,
            username = profile.username,
            email = profile.email,
            name = profile.name,
            role = profile.role,
            profilePictureUrl = profile.profilePictureUrl
        )
    }

    override suspend fun getUserRole(): String? = userDao.getProfile()?.role

    override suspend fun logout() {
        secureStore.clearSession()
        userDao.clearProfile()
    }

    override suspend fun isLoggedIn(): Boolean = secureStore.getAccessTokenOnce() != null
}