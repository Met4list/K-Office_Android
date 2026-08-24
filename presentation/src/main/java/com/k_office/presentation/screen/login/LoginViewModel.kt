package com.k_office.presentation.screen.login

import com.k_office.domain.model.AuthTypeModel
import com.k_office.domain.use_case.AuthorizationUseCase
import com.k_office.domain.use_case.GetCurrentUserUseCase
import com.k_office.presentation.base.utils.Event
import com.k_office.presentation.base.utils.SmsRetrieverCoordinator
import com.k_office.presentation.base.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authorizationUseCase: AuthorizationUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val smsRetrieverCoordinator: SmsRetrieverCoordinator,
) : BaseViewModel() {

    private val _authType = MutableStateFlow<Event<AuthTypeModel>?>(null)
    val authType = _authType.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    init {
        launchWithResponseState(block = { getCurrentUserUseCase.invoke(Unit) }) {
            _isLoggedIn.emit(it != null)
        }
    }

    fun login(telephoneNumber: String) {
        launchWithResponseState(
            block = {
                // Спочатку слухач, потім send-otp — інакше GMS віддає SMS без receiver
                smsRetrieverCoordinator.startListening()
                authorizationUseCase.invoke(telephoneNumber)
            }
        ) {
            _authType.emit(Event(it))
        }
    }

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val phoneRegex = Regex("^\\+380\\d{9}$")
        return phoneNumber.matches(phoneRegex) && phoneNumber.length == 13
    }
}