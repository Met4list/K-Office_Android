package com.k_office.presentation.base.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.k_office.domain.base.DataState
import com.k_office.domain.base.ResponseState
import com.k_office.domain.base.UIText
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel: ViewModel() {

    protected val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable)
        emitError(throwable.message ?: "Unknown error")
        _loading.value = false
    }

    protected val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    protected val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    protected val _uiTextMessage = MutableStateFlow<UIText?>(null)
    val uiTextMessage = _uiTextMessage.asStateFlow()

    private fun emitError(message: String) {
        viewModelScope.launch {
            _errorMessage.emit(message)
        }
    }

    protected fun <T> launchWithResponseState(
        dispatcher: CoroutineContext = Dispatchers.IO,
        block: suspend () -> Flow<DataState<T>>,
        onSuccess: suspend (T) -> Unit
    ) {
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            block.invoke().collect {
                when (it) {
                    DataState.Default -> _loading.emit(false)
                    is DataState.Failure -> {
                        _loading.emit(false)
                        _uiTextMessage.emit(it.errorInfo)
                    }
                    DataState.Loading -> _loading.emit(true)
                    is DataState.Success<T> -> {
                        _loading.emit(false)
                        it.data?.let { result -> onSuccess.invoke(result) }
                    }
                }
            }
        }
    }
}