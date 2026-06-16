package com.example.Roomdb.data.repositoryimpl

import android.util.Log
import com.example.Roomdb.api.ApiService
import com.example.Roomdb.data.PostDao
import com.example.Roomdb.domain.repository.PostRepository
import com.example.Roomdb.model.Post
import kotlinx.coroutines.flow.Flow

class PostRepositoryImpl(
    private val postDao: PostDao,
    private val apiService: ApiService
) : PostRepository {

    companion object {
        private const val TAG = "PostRepository"
    }

    // Always return from cache (Room)
    override fun getPosts(): Flow<List<Post>> {
        Log.d(TAG, "Getting posts from cache")
        return postDao.getAllPosts()
    }

    // Fetch from network and update cache
    override suspend fun refreshPosts() {
        try {
            Log.d(TAG, "Refreshing posts from API")
            val posts = apiService.getPosts()
            postDao.insertPosts(posts)
            Log.d(TAG, "Successfully refreshed ${posts.size} posts")
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing posts", e)
            throw e // Re-throw for ViewModel to handle
        }
    }

    // Force fetch ignoring cache
    override suspend fun fetchFreshPosts(): List<Post> {
        try {
            Log.d(TAG, "Fetching fresh posts from API")
            val posts = apiService.getPosts()
            postDao.deleteAllPosts() // Clear old data
            postDao.insertPosts(posts)
            Log.d(TAG, "Successfully fetched ${posts.size} fresh posts from api")
            return posts
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching fresh posts", e)
            throw e
        }
    }

    // Clear cache
    override suspend fun clearCache() {
        try {
            Log.d(TAG, "Clearing cache")
            postDao.deleteAllPosts()
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing cache", e)
            throw e
        }
    }
}