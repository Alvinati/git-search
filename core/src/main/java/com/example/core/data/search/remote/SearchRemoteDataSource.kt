package com.example.core.data.search.remote

import com.example.core.data.util.Response
import com.example.core.data.util.WebServiceClient
import javax.inject.Inject

interface SearchRemoteDataSource {
    suspend fun getUsernames(searchKey: String, page: Int, maxPageItem: Int = 30)
    : Response<UserResponse>
}

class SearchRemoteSource @Inject constructor(
    private val client: WebServiceClient
) : SearchRemoteDataSource {
    override suspend fun getUsernames(searchKey: String, page: Int, maxPageItem: Int)
    : Response<UserResponse>{

        return client.call("/search/users")
            .addQueryParam("q", searchKey)
            .addQueryParam("page", page.toString())
            .addQueryParam("per_page", maxPageItem.toString())
            .addQueryParam("order", "desc")
            .requestGet()
            .execute()
    }

}