package com.example.core.data.search.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllUsers(users: List<UserEntity>)

    @Query("SELECT * FROM users WHERE name LIKE :key ORDER BY id LIMIT :limit OFFSET :offset ")
    suspend fun getUsers(key: String, limit: Int, offset: Int) : List<UserEntity>

    @Query("DELETE FROM users")
    suspend fun clearAll()
}