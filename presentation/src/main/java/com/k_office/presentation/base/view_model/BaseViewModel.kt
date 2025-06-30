package com.k_office.presentation.base.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.k_office.domain.base.ResponseState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
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

    private fun emitError(message: String) {
        viewModelScope.launch {
            _errorMessage.emit(message)
        }
    }

    protected fun <T> launchWithResponseState(
        dispatcher: CoroutineContext = Dispatchers.IO,
        block: suspend () -> ResponseState<T>,
        onSuccess: suspend (T) -> Unit
    ) {
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            _loading.value = true

            when (val result = block()) {

                is ResponseState.Success -> {
                    onSuccess(result.data)
                    _loading.value = false
                }

                is ResponseState.Error -> {
                    emitError(result.throwable?.message ?: "Unknown error")
                    _loading.value = false
                }

                is ResponseState.Loading -> {
                    _loading.value = true
                }
            }
        }
    }
}