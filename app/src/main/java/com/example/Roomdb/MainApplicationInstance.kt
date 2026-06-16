package com.example.Roomdb

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.example.Roomdb.api.RetrofitInstance
import com.example.Roomdb.data.PostsDatabase
import com.example.Roomdb.data.repositoryimpl.PostRepositoryImpl
import com.example.Roomdb.domain.repository.PostRepository

class MainApplicationInstance : Application() {

    companion object {
        private const val TAG = "MainApplication"
        lateinit var postsDatabase: PostsDatabase
        lateinit var postRepository: PostRepository
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application started")

        // Initialize Database
        postsDatabase = Room.databaseBuilder(
            applicationContext,
            PostsDatabase::class.java,
            PostsDatabase.NAME
        ).build()

        // Initialize Repository
        postRepository = PostRepositoryImpl(
            postDao = postsDatabase.getPosts(),
            apiService = RetrofitInstance.api
        )

        Log.d(TAG, "Repository initialized")
    }
}