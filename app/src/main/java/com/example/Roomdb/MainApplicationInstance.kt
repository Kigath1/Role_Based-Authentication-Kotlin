package com.example.Roomdb

import android.app.Application
import androidx.room.Room
import com.example.Roomdb.data.PostsDatabase

class MainApplicationInstance : Application() {

    companion object {
        lateinit var postsDatabase: PostsDatabase
    }

    override fun onCreate() {
        super.onCreate()
        postsDatabase = Room.databaseBuilder(
            applicationContext,
            PostsDatabase::class.java,
            PostsDatabase.NAME
        ).build()
    }

}