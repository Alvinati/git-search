package com.example.gitsearch.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gitsearch.helper.StateObserveOnce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel<Event, State> : ViewModel(), CoroutineScope {

    private val parentJob = SupervisorJob()

    private val _state = MutableLiveData<StateObserveOnce<State>>()
    val state : LiveData<StateObserveOnce<State>>
        get() = _state

    override val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    abstract fun onEventReceived(event: Event)

    protected fun pushState(state: State) {
        _state.value = StateObserveOnce(state)
    }


    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
    protected  fun slideAdsTimer(listSize: Int) = flow {
        var position = 0
        delay(1000)
        while (true) {
            position = if(position < 0) 0 else if(position > listSize) 0 else position
            emit(position)
            delay(5000)
            position++
        }
    }

}