package com.example.Roomdb.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val userId: String,
    val username: String,
    val email: String,
    val name: String,
    val role: String,
    val profilePictureUrl: String?
)