package com.k_office.presentation.screen.login

import androidx.lifecycle.viewModelScope
import com.k_office.domain.base.DataState
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

    private val _phoneNumber = MutableSharedFlow<String>()
    val phoneNumber = _phoneNumber.asSharedFlow()

    fun login(telephoneNumber: String) {
        runCatching {
            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                _loading.emit(true)
                authorizationUseCase.invoke(telephoneNumber).collect {
                    when (it) {
                        DataState.Default -> _loading.emit(false)
                        is DataState.Failure -> _loading.emit(false)
                        DataState.Loading -> _loading.emit(true)
                        is DataState.Success -> {
                            _loading.emit(false)
                            _phoneNumber.emit(telephoneNumber)
                        }
                    }
                }
            }
        }.onFailure {
            Timber.e(it)
        }
    }
}