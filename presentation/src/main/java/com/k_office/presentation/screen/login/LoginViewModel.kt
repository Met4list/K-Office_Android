package com.k_office.presentation.screen.login

import com.k_office.domain.model.AuthTypeModel
import com.k_office.domain.use_case.AuthorizationUseCase
import com.k_office.domain.use_case.GetCurrentUserUseCase
import com.k_office.presentation.base.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authorizationUseCase: AuthorizationUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : BaseViewModel() {

    private val _authType = MutableStateFlow<AuthTypeModel?>(null)
    val authType = _authType.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    init {
        launchWithResponseState(block = { getCurrentUserUseCase.invoke(Unit) }) {
            _isLoggedIn.emit(it != null)
        }
    }

    fun login(telephoneNumber: String) {
        launchWithResponseState(block = { authorizationUseCase.invoke(telephoneNumber) }) {
            _authType.emit(it)
        }
    }

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val phoneRegex = Regex("^\\+380\\d{9}$")
        return phoneNumber.matches(phoneRegex) && phoneNumber.length == 13
    }
}