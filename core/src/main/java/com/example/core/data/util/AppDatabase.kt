package com.example.core.data.util

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.core.data.search.local.SearchDao
import com.example.core.data.search.local.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun searchDao() : SearchDao

    companion object {
        private const val dbName = "git-db"

        fun buildDatabase(context: Context) : AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, dbName)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}