package com.k_office.presentation.screen.login

import androidx.lifecycle.viewModelScope
import com.k_office.domain.base.DataState
import com.k_office.domain.use_case.RegistrationUseCase
import com.k_office.presentation.base.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val registrationUseCase: RegistrationUseCase
) : BaseViewModel() {

    private val _isSuccessfulyRegistered = MutableStateFlow(false)
    val isSuccessfulyRegistered = _isSuccessfulyRegistered.asStateFlow()

    fun registrationBonus(telephone: String, name: String, address: String) {

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val requestParams = mapOf(
                "telephoneNumber" to telephone,
                "address" to address,
                "fullName" to name
            )
            registrationUseCase.invoke(requestParams).collect {
                when (it) {
                    DataState.Default -> Unit
                    is DataState.Failure -> {
                        _loading.emit(false)
                        _isSuccessfulyRegistered.emit(false)
                        _uiTextMessage.emit(it.errorInfo)
                    }

                    DataState.Loading -> _loading.emit(true)
                    is DataState.Success<*> -> {
                        _loading.emit(false)
                        _isSuccessfulyRegistered.emit(true)
                    }
                }
            }
        }
    }
}