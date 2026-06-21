package com.example.Roomdb.data.local

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [UserProfileEntity::class], version = 1)
abstract  class AppDatabase : RoomDatabase() {

    companion object {
        const val NAME = "Kazi_User_Db"
    }

    abstract fun userDao() : UserDao
}