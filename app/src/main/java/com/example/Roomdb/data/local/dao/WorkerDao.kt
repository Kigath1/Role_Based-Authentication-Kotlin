package com.example.Roomdb.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.Roomdb.data.local.entities.WorkerEntity

@Dao
interface WorkerDao {

//    @Query("SELECT * FROM workers ORDER BY averageRating DESC")
@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun insertAll(workers: List<WorkerEntity>)

    @Query("SELECT * FROM workers")
    suspend fun getAllWorkers(): List<WorkerEntity>

    @Query("DELETE FROM workers")
    suspend fun clearAll()
}
