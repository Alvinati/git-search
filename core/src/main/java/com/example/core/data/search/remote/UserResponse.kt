package com.example.core.data.search.remote

import com.example.core.data.search.local.UserEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserResponse (
    val id: Long,
    val login: String,
    @Json(name = "avatar_url")
    val avatarUrl: String,
    @Json(name = "html_url")
    val htmlUrl : String
) {
    fun toEntity() : UserEntity {
       return UserEntity(id, login, avatarUrl, htmlUrl)
    }
}

