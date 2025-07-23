package com.k_office.presentation.screen.auth

import androidx.lifecycle.viewModelScope
import com.k_office.domain.base.DataState
import com.k_office.domain.base.ResponseState
import com.k_office.domain.data_source.CurrentUserInfoDataSource
import com.k_office.domain.data_source.KOfficeDataSource
import com.k_office.domain.model.CurrentUserInfoModel
import com.k_office.domain.use_case.AuthorizationUseCase
import com.k_office.presentation.base.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authorizationUseCase: AuthorizationUseCase
) : BaseViewModel() {

    private val _onSuccess = MutableSharedFlow<Boolean>()
    val onSuccess = _onSuccess.asSharedFlow()

    fun login(telephoneNumber: String) {
        runCatching {
            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                _loading.emit(true)
                authorizationUseCase.invoke(telephoneNumber).collect {
                    when (it) {
                        DataState.Default -> Unit
                        is DataState.Failure -> _onSuccess.emit(false)
                        DataState.Loading -> _loading.emit(true)
                        is DataState.Success<*> -> {
                            _loading.emit(false)
                            _onSuccess.emit(true)
                        }
                    }
                }
            }.invokeOnCompletion {
                sendFCMToken()
            }
        }.onFailure {
            Timber.e(it)
        }
    }

    private fun sendFCMToken() {
        // TODO implement this functionality to provide token to server
    }
}