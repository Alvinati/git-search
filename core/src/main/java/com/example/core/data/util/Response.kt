package com.example.core.data.util

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class Response<Data>(
        var total_count : Int = 0,
        var incomplete_results: Boolean = false,
        var items: List<Data>? = null
)
