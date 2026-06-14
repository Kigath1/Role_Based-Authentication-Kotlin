package com.example.Roomdb.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.Roomdb.model.Post
import kotlinx.coroutines.flow.Flow


@Dao
interface PostDao {

    @Query("SELECT * FROM Post")
    fun getAllPosts(): Flow<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<Post>)
}