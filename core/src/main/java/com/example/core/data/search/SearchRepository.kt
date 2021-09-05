package com.example.core.data.search

import com.example.core.data.search.remote.SearchRemoteDataSource
import com.example.core.data.util.AppDatabase
import com.example.core.data.util.Cons
import com.example.core.model.Result
import com.example.core.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject

interface SearchRepository {
    fun getSearchUser(searchKey: String, page: Int) : Flow<Result<List<User>>>
}

class SearchRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val remote: SearchRemoteDataSource
) : SearchRepository {

    override fun getSearchUser(searchKey: String, page: Int): Flow<Result<List<User>>> {
        return flow {
            val offset = if(page == 1) 0 else (page-1)*Cons.SEARCH_USER_PER_PAGE
            val local = appDatabase.searchDao()
            val cached = local.getUsers("%$searchKey%",Cons.SEARCH_USER_PER_PAGE,  offset).map { it.toModel() }

            emit(Result.Loading(cached))

            if(cached.isNullOrEmpty() || cached.size < Cons.SEARCH_USER_PER_PAGE){
                try{
                    val response = remote.getUsernames(searchKey, page)
                    if(!response.items.isNullOrEmpty()) {
                        local.insertAllUsers(response.items!!.map { it.toEntity() })
                        val saved = local.getUsers("%$searchKey%",Cons.SEARCH_USER_PER_PAGE,  offset).map { it.toModel() }
                        emit(Result.Success(saved))
                    }
                } catch (ex: Exception) {
                    emit(Result.Error<List<User>>(ex))
                }

            }
        }
    }

}