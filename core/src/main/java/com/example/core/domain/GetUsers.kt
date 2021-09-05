package com.example.core.domain

import com.example.core.data.search.SearchRepository
import com.example.core.di.IoDispatcher
import com.example.core.domain.util.FlowUseCase
import com.example.core.model.Result
import com.example.core.model.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import java.lang.IllegalStateException
import javax.inject.Inject


class GetUsers @Inject constructor(
    private val searchRepository: SearchRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<GetUserParams, List<User>>(dispatcher){

    override fun execute(parameters: GetUserParams?): Flow<Result<List<User>>> {
        if(parameters == null)
            throw IllegalStateException("parameters cannot be null")

        return searchRepository.getSearchUser(parameters.key, parameters.page)
    }
}

data class GetUserParams(
    val key: String,
    val page: Int
)