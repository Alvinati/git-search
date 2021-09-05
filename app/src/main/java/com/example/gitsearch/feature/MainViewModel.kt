package com.example.gitsearch.feature

import androidx.lifecycle.viewModelScope
import com.example.core.domain.GetUserParams
import com.example.core.domain.GetUsers
import com.example.core.model.HttpException
import com.example.core.model.Result
import com.example.core.model.User
import com.example.gitsearch.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUsers: GetUsers
) : BaseViewModel<MainViewModel.Event, MainViewModel.State>(){

    sealed class Event{
        class SearchUser(val searchKey: String) : Event()
        class LoadMore(val searchKey: String) : Event()
    }

    sealed class State{
        class ShowToast(val message: String) : State()
        class ShowSearchResult(val items: List<User>) :State()
        class LoadMoreResult(val items: List<User>) :State()
        object ShowLoading : State()
        object HideLoading : State()
    }

    private var isLoading = false
    override fun onEventReceived(event: Event) {
        when(event){
            is Event.SearchUser -> {
                if(event.searchKey.length < 3)
                    return
                currentPage = 1
                loadData(event.searchKey, currentPage, false)
            }
            is Event.LoadMore -> {
                if(isLoading)
                    return

                if(event.searchKey.length < 3)
                    return
                isLoading = true
                loadData(event.searchKey, ++currentPage, true)
            }
        }
    }

    private var currentPage = 1
    private var loadMoreJob : Job? = null

    override fun onCleared() {
        super.onCleared()
        loadMoreJob?.cancel()
    }

    private fun loadData(searchKey: String, page: Int, isLoadMore: Boolean) {
        loadMoreJob = getUsers.invoke(GetUserParams(searchKey, page))
            .onEach { processResult(it, isLoadMore)
            }.launchIn(viewModelScope)
    }

    private fun processResult(result: Result<List<User>>, isLoadMore: Boolean) {
        when(result){
            is Result.Loading -> {
                if(result.data.isNullOrEmpty())
                    pushState(State.ShowLoading)
                else {
                    if(!isLoadMore)
                        pushState(State.ShowSearchResult(result.data!!))
                    else pushState(State.LoadMoreResult(result.data!!))
                }
            }
            is Result.Success -> {
                pushState(State.HideLoading)
                if(result.data.isNotEmpty()) {
                    if(!isLoadMore)
                        pushState(State.ShowSearchResult(result.data!!))
                    else pushState(State.LoadMoreResult(result.data!!))
                }
            }
            is Result.Error -> {
                pushState(State.HideLoading)
                if(result.ex is HttpException) {
                    if((result.ex as HttpException).httpError() is HttpException.HttpErrors.ServiceUnavailable)
                        pushState(State.ShowToast("Tidak dapat terhubung ke internet"))
                }
            }
        }
        isLoading = false
    }
}