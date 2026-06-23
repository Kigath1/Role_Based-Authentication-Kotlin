package com.example.Roomdb.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.Roomdb.data.local.converters.Converters
import com.example.Roomdb.data.local.dao.UserDao
import com.example.Roomdb.data.local.dao.WorkerDao
import com.example.Roomdb.data.local.entities.UserProfileEntity
import com.example.Roomdb.data.local.entities.WorkerEntity

@TypeConverters(Converters::class)
@Database(
    entities = [UserProfileEntity::class, WorkerEntity::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun workerDao(): WorkerDao

    companion object {
        const val NAME = "Kazi_User_Db"
    }
}