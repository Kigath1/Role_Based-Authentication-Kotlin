package com.example.Roomdb.data.model

import androidx.room.PrimaryKey

data class UserProfile(
    val userId: String,
    val username: String,
    val email: String,
    val name: String,
    val role: String,
    val profilePictureUrl: String?
)