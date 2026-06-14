package com.example.Roomdb.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.Roomdb.model.Post


@Database(entities = [Post::class], version = 1)
abstract  class PostsDatabase : RoomDatabase() {

    companion object {
        const val NAME = "PostsDb"
    }

    abstract fun getPosts() : PostDao
}