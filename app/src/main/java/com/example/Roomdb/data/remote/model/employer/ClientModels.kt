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
        val fullName: String,
        val email: String? = null,
        val phoneNumber: String,
        val location: String? = null
    )
}