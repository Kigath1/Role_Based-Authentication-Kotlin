package com.example.Roomdb.domain.repository

import com.example.Roomdb.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    // Get posts from cache (Room database)
    fun getPosts(): Flow<List<Post>>

    // Fetch from API and update cache
    suspend fun refreshPosts()

    // Force fresh fetch ignoring cache
    suspend fun fetchFreshPosts(): List<Post>

    // Clear all cached data
    suspend fun clearCache()
}