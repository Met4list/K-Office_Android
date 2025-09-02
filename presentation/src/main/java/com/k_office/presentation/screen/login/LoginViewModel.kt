package com.k_office.presentation.screen.login

import com.k_office.domain.use_case.AuthorizationUseCase
import com.k_office.presentation.base.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authorizationUseCase: AuthorizationUseCase,
) : BaseViewModel() {

    private val _phoneNumber = MutableSharedFlow<String>()
    val phoneNumber = _phoneNumber.asSharedFlow()

    fun login(telephoneNumber: String) {
        launchWithResponseState(block = { authorizationUseCase.invoke(telephoneNumber) }) {
            _phoneNumber.emit(telephoneNumber)
        }
    }

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val phoneRegex = Regex("^\\+380\\d{9}$")
        return phoneNumber.matches(phoneRegex) && phoneNumber.length == 13
    }
}