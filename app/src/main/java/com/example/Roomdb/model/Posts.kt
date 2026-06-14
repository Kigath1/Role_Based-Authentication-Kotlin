package com.example.Roomdb.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Post(
    @PrimaryKey
    val id: Int,
    val title: String,
    val body: String
)
