package com.example.Roomdb.api

import com.example.Roomdb.model.Post
import retrofit2.http.GET

interface ApiService {
    @GET("posts")
    suspend fun getPosts() : List<Post>
}