package com.example.Roomdb.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData
)

@Serializable
data class LoginData(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String,
    @SerialName("userId") val userId: String,
    @SerialName("username") val username: String,
    @SerialName("email") val email: String,
    @SerialName("name") val name: String,
    @SerialName("role") val role: String,            // "WORKER" or "CLIENT"
    @SerialName("profilePictureUrl") val profilePictureUrl: String? = null
)
