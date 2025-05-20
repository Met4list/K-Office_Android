package com.k_office.presentation.screen.auth

import androidx.lifecycle.viewModelScope
import com.k_office.domain.base.ResponseState
import com.k_office.domain.data_source.CurrentUserInfoDataSource
import com.k_office.domain.data_source.KOfficeDataSource
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
    private val kOfficeDataSource: KOfficeDataSource,
    private val getCurrentUserInfoDataSource: CurrentUserInfoDataSource
) : BaseViewModel() {

    private val _onSuccess = MutableSharedFlow<Boolean>()
    val onSuccess = _onSuccess.asSharedFlow()

    fun login(telephoneNumber: String) {
        runCatching {
            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                _loading.emit(true)
                when (val response = kOfficeDataSource.getBalanceBonus(telephoneNumber)) {
                    is ResponseState.Success -> {
                        if (response.data != null) {
                            getCurrentUserInfoDataSource.insertUser(response.data!!)
                            _onSuccess.emit(true)
                            _loading.emit(false)
                        } else {
                            _onSuccess.emit(false)
                            _loading.emit(false)
                        }
                    }
                    is ResponseState.Error -> {
                        _onSuccess.emit(false)
                        _loading.emit(false)
                    }
                }
            }
        }.onFailure {
            Timber.e(it)
        }
    }
}