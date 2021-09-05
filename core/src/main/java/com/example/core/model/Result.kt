package com.example.core.model

sealed class Result<T>(
) {
    class Loading<T>(val data: T? = null) : Result<T>()
    class Success<T>(val data : T) : Result<T>()
    class Error<T>(val ex: Exception, val data:T? = null) : Result<T>()
}