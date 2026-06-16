package com.example.Roomdb.domain.usecases

import com.example.Roomdb.domain.repository.PostRepository
import com.example.Roomdb.model.Post
import kotlinx.coroutines.flow.Flow

class GetPostsUseCase(
    private val repository: PostRepository
) {
    operator fun invoke(): Flow<List<Post>> {
        return repository.getPosts()
    }
}