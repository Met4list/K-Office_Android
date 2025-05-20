package com.k_office.presentation.screen.login

import androidx.lifecycle.viewModelScope
import com.k_office.domain.base.ResponseState
import com.k_office.domain.data_source.KOfficeDataSource
import com.k_office.domain.model.CardInformationModel
import com.k_office.domain.use_case.GetCurrentUserInfoUseCase
import com.k_office.presentation.base.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val kOfficeDataSource: KOfficeDataSource,
    private val getCurrentUserInfoUseCase: GetCurrentUserInfoUseCase
) : BaseViewModel() {

    private val _registrationBonus = MutableSharedFlow<CardInformationModel>()
    val registrationBonus = _registrationBonus.asSharedFlow()

    fun registrationBonus(telephone: String, name: String, address: String) {
        runCatching {
            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                _loading.emit(true)
                when (val response =
                    kOfficeDataSource.registrationBonus(telephone, name, address)) {
                    is ResponseState.Success -> {
                        _loading.emit(false)
                        getCurrentUserInfoUseCase.invoke(telephone)
                        _registrationBonus.emit(response.data!!)
                    }

                    is ResponseState.Error -> {
                        _loading.emit(false)
                        _errorMessage.emit(response?.throwable?.message!!)
                    }
                }
            }
        }.onFailure {
            Timber.e(it)
        }
    }
}