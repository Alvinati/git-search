package com.example.core.data.search.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Long,
    val name : String,
    @ColumnInfo(name= "avatar_url")
    val avatarUrl : String,
    @ColumnInfo(name= "html_url")
    val htmlUrl : String
) {
    fun toModel() : User {
        return User(id, name, avatarUrl, htmlUrl)
    }
}