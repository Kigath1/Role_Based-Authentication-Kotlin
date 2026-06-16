package com.example.Roomdb.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomdb.domain.repository.PostRepository
import com.example.Roomdb.domain.usecases.GetPostsUseCase
import com.example.Roomdb.domain.usecases.RefreshPostsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PostViewModel(
    private val repository: PostRepository
) : ViewModel() {

    companion object {
        private const val TAG = "PostViewModel"
    }

    // Use Cases
    private val getPostsUseCase = GetPostsUseCase(repository)
    private val refreshPostsUseCase = RefreshPostsUseCase(repository)

    // UI State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isCacheEmpty = MutableStateFlow(false)
    val isCacheEmpty: StateFlow<Boolean> = _isCacheEmpty.asStateFlow()

    // Posts from cache (always observable)
    val postsList = getPostsUseCase()
        .stateIn(
            viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        Log.d(TAG, "ViewModel initialized")
        loadPosts()
    }

    // Load posts - check cache first
    fun loadPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Check if cache has data
                val currentPosts = postsList.value
                _isCacheEmpty.value = currentPosts.isEmpty()

                if (currentPosts.isEmpty()) {
                    Log.d(TAG, "Cache is empty, fetching from network")
                    refreshPostsUseCase()
                } else {
                    Log.d(TAG, "Cache has ${currentPosts.size} posts, showing cached data")
                    // Cache has data - display immediately
                    // Optionally refresh in background if data is old
                }
            } catch (e: Exception) {
                _error.value = "Failed to load posts: ${e.message}"
                Log.e(TAG, "Error loading posts", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Manual refresh (pull-to-refresh)
    fun refreshPosts() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _error.value = null

            try {
                Log.d(TAG, "Manual refresh triggered")
                refreshPostsUseCase()
                _isCacheEmpty.value = false
            } catch (e: Exception) {
                _error.value = "Failed to refresh: ${e.message}"
                Log.e(TAG, "Error refreshing posts", e)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    // Clear cache
    fun clearCache() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Clearing cache")
                repository.clearCache()
                _isCacheEmpty.value = true
                _error.value = null
                // Reload after clearing
                loadPosts()
            } catch (e: Exception) {
                _error.value = "Failed to clear cache: ${e.message}"
                Log.e(TAG, "Error clearing cache", e)
            }
        }
    }

    // Retry after error
    fun retry() {
        loadPosts()
    }
}