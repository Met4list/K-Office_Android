package com.k_office.presentation.screen.login

import androidx.lifecycle.viewModelScope
import com.k_office.domain.base.ResponseState
import com.k_office.domain.data_source.KOfficeDataSource
import com.k_office.domain.use_case.GetCurrentUserInfoUseCase
import com.k_office.presentation.base.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val kOfficeDataSource: KOfficeDataSource,
    private val getCurrentUserInfoUseCase: GetCurrentUserInfoUseCase
) : BaseViewModel() {

    private val _isSuccessfulyRegistered = MutableStateFlow<Boolean>(false)
    val isSuccessfulyRegistered = _isSuccessfulyRegistered.asStateFlow()

    fun registrationBonus(telephone: String, name: String, address: String) {
        runCatching {
            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                _loading.emit(true)
                when (val response =
                    kOfficeDataSource.registrationBonus(telephone, name, address)) {
                    is ResponseState.Success -> {
                        _loading.emit(false)
                        getCurrentUserInfoUseCase.invoke(telephone)
                        _isSuccessfulyRegistered.emit(true)
                    }

                    is ResponseState.Loading -> {

                    }

                    is ResponseState.Error -> {
                        _loading.emit(false)
                        _errorMessage.emit(response?.throwable?.message!!)
                        _isSuccessfulyRegistered.emit(false)
                    }
                }
            }
        }.onFailure {
            Timber.e(it)
        }
    }
}