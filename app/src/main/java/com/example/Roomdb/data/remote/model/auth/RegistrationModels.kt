package com.example.Roomdb.data.remote.model.auth

import kotlinx.serialization.Serializable

object RegistrationModels {

    @Serializable
    data class RegisterRequest(
        val username: String,
        val email: String,
        val password: String,
        val firstName: String,
        val secondName: String,
        val role: String           // "Client" or "Worker" — API spec uses capitalized
    )

    @Serializable
    data class RegisterResponse(
        val success: Boolean,
        val message: String,
        val data: String? = null   // API returns data: null, so nullable String covers it
    )

    // Generic wrapper: register, verify, resend all return { success, message, data: null }
    @Serializable
    data class ApiResponse(
        val success: Boolean,
        val message: String
    )
}