package com.example.Roomdb.domain.usecases


import com.example.Roomdb.domain.repository.PostRepository
import com.example.Roomdb.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class GetPostsWithAutoRefreshUseCase(
    private val getPostsUseCase: GetPostsUseCase,
    private val refreshPostsUseCase: RefreshPostsUseCase
) {
    operator fun invoke(forceRefresh: Boolean = false): Flow<List<Post>> {
        return getPostsUseCase().map { posts ->
            if (posts.isEmpty() || forceRefresh) {
                // Trigger refresh in background
                // This is a side effect - but needed for auto-refresh
                // In production, use a more sophisticated approach
                kotlinx.coroutines.GlobalScope.launch {
                    refreshPostsUseCase()
                }
            }
            posts
        }
    }
}