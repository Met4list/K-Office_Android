package com.k_office.presentation.screen.verify_otp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.k_office.domain.base.DataState
import com.k_office.domain.use_case.AuthorizationUseCase
import com.k_office.domain.use_case.VerifyOtpUseCase
import com.k_office.domain.use_case.VerifyRegisterUseCase
import com.k_office.presentation.base.utils.SMSHelper
import com.k_office.presentation.base.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OtpVerificationViewModel @Inject constructor(
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val verifyRegisterUseCase: VerifyRegisterUseCase,
    private val authorizationUseCase: AuthorizationUseCase,
) : BaseViewModel() {

    private val _onSuccess = MutableStateFlow(false)
    val onSuccess = _onSuccess.asStateFlow()

    private val _retryOtp = MutableSharedFlow<Boolean>()
    val retryOtp = _retryOtp.asSharedFlow()

    private val _otpState = MutableStateFlow<String?>(null)
    val otpState = _otpState.asStateFlow()

    private val _smsPermissionGranted = MutableStateFlow(false)
    val smsPermissionGranted = _smsPermissionGranted.asStateFlow()

    private val _statusMessage = MutableStateFlow("Waiting for OTP...")
    val statusMessage = _statusMessage.asStateFlow()

    fun checkSMSPermissions(context: Context) {
        val smsPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS)
        val readPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS)

        val hasPermissions = smsPermission == PackageManager.PERMISSION_GRANTED &&
                readPermission == PackageManager.PERMISSION_GRANTED

        _smsPermissionGranted.value = hasPermissions

        if (hasPermissions) {
            _statusMessage.value = "SMS permissions granted. Waiting for OTP..."
        } else {
            _statusMessage.value = "SMS permissions required for auto-detection"
        }
    }

    fun onSMSPermissionsResult(permissions: Map<String, Boolean>) {
        val hasPermissions = permissions[Manifest.permission.RECEIVE_SMS] == true &&
                permissions[Manifest.permission.READ_SMS] == true

        _smsPermissionGranted.value = hasPermissions

        if (hasPermissions) {
            _statusMessage.value = "Permissions granted. Waiting for OTP..."
        } else {
            _statusMessage.value = "SMS permissions denied. Please enter OTP manually."
        }
    }

    fun verifyOtp(phoneNumber: String, otp: String) {
        launchWithResponseState(block = { verifyOtpUseCase.invoke(Pair(phoneNumber, otp)) }) {
            _onSuccess.emit(true)
        }
    }

    fun verifyRegister(phoneNumber: String, otp: String) {
        launchWithResponseState(block = { verifyRegisterUseCase.invoke(Pair(phoneNumber, otp)) }) {
            _onSuccess.emit(true)
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


    fun onOtpReceived(otp: String?) {
        viewModelScope.launch {
            _otpState.emit(otp)
            if (!otp.isNullOrEmpty()) {
                _statusMessage.emit("OTP введен: $otp")
            }
        }
    }

    fun onSMSReceived(message: String) {
        try {
            Timber.d("SMS received: $message")
            val extractedOTP = SMSHelper.extractOTP(message)
            if (extractedOTP.isNotEmpty() && SMSHelper.isValidOTP(extractedOTP)) {
                viewModelScope.launch {
                    _otpState.emit(extractedOTP)
                    _statusMessage.emit("OTP получен автоматически: $extractedOTP")
                }
            } else {
                _statusMessage.value = "Не удалось извлечь OTP из SMS"
            }
        } catch (e: Exception) {
            Timber.d("Error processing SMS: ${e.message}")
            _statusMessage.value = "Ошибка обработки SMS"
        }
    }

    fun clearOTP() {
        _otpState.value = ""
        _statusMessage.value = "Waiting for OTP..."
    }
}