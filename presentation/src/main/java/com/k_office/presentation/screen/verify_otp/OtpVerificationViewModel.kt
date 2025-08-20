package com.k_office.presentation.screen.verify_otp

import androidx.lifecycle.viewModelScope
import com.k_office.domain.base.DataState
import com.k_office.domain.use_case.AuthorizationUseCase
import com.k_office.domain.use_case.VerifyOtpUseCase
import com.k_office.presentation.base.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpVerificationViewModel @Inject constructor(
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val authorizationUseCase: AuthorizationUseCase
) : BaseViewModel() {

    private val _onSuccess = MutableStateFlow(false)
    val onSuccess = _onSuccess.asStateFlow()

    private val _retryOtp = MutableSharedFlow<Boolean>()
    val retryOtp = _retryOtp.asSharedFlow()

    fun verifyOtp(phoneNumber: String, otp: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            verifyOtpUseCase.invoke(Pair(phoneNumber, otp)).collect {
                when (it) {
                    DataState.Default -> {
                        _loading.emit(false)
                    }

                    is DataState.Failure -> {
                        _loading.emit(false)
                        _onSuccess.emit(false)
                    }

                    DataState.Loading -> {
                        _loading.emit(true)
                    }

                    is DataState.Success -> {
                        _onSuccess.emit(true)
                    }
                }
            }
        }
    }

    fun retryOtp(phoneNumber: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            authorizationUseCase.invoke(phoneNumber).collect {
                when (it) {
                    DataState.Default -> {
                        _loading.emit(false)
                    }
                    is DataState.Failure -> {
                        _loading.emit(false)
                        _retryOtp.emit(false)
                    }
                    DataState.Loading -> {
                        _loading.emit(true)
                    }
                    is DataState.Success -> {
                        _retryOtp.emit(true)
                    }
                }
            }
        }
    }
}