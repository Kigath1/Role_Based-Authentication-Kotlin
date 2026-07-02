package com.example.Roomdb.data.remote.model.employer

import kotlinx.serialization.Serializable

object ClientModels {

    @Serializable
    data class ClientProfileRequest(
        val fullName: String,
        val phoneNumber: String,
        val location: String
    )

    @Serializable
    data class ClientProfileResponse(
        val id: String,
        val userId: String? = null,
        val username: String? = null,
        val email: String,
        val fullName: String,
        val phoneNumber: String? = null,
        val location: String? = null,
        val createdAt: String? = null
    )
}