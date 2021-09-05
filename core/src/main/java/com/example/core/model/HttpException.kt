package com.example.core.model

import java.lang.Exception

class HttpException(private val code: Int, private val errorMessage: String?) : Exception() {

    fun httpError() : HttpErrors {
      return when(code) {
          304 -> HttpErrors.NotModified(errorMessage)
          422 -> HttpErrors.UnprocessedEntity(errorMessage)
          500 -> HttpErrors.InternalServerError(errorMessage)
          502 -> HttpErrors.BadGateWay(errorMessage)
          503 -> HttpErrors.ServiceUnavailable(errorMessage)
          else -> HttpErrors.Error("Undefined Error: $code")
        }
    }

    sealed class HttpErrors {
        data class NotModified(val exception: String?) : HttpErrors()
        data class UnprocessedEntity(val exception: String?) : HttpErrors()
        data class InternalServerError(val exception: String?) : HttpErrors()
        data class BadGateWay(val exception: String?) : HttpErrors()
        data class ServiceUnavailable(val exception: String?) : HttpErrors()
        data class Error(val exception: String): HttpErrors()
    }

}