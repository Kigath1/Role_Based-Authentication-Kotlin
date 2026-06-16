package com.example.Roomdb.domain.usecases

import com.example.Roomdb.domain.repository.PostRepository

class RefreshPostsUseCase(
    private val repository: PostRepository
) {
    suspend operator fun invoke() {
        repository.refreshPosts()
    }
}