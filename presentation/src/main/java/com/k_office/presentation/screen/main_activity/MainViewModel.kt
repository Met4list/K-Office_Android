package com.k_office.presentation.screen.main_activity

import androidx.lifecycle.viewModelScope
import com.k_office.domain.data_source.TokenDataSource
import com.k_office.domain.use_case.GetCurrentUserUseCase
import com.k_office.presentation.base.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : BaseViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _tokenExpiredEvent = MutableStateFlow(Unit)
    val tokenExpiredEvent = _tokenExpiredEvent.asStateFlow()

    init {
        checkUserLoggedIn()
    }

    private fun checkUserLoggedIn() {
        viewModelScope.launch {
            try {
                launchWithResponseState(block = { getCurrentUserUseCase.invoke(Unit) }) { currentUser ->
                    val isLoggedIn = currentUser != null
                    Timber.d("Current user check result: $isLoggedIn")
                    _isLoggedIn.emit(isLoggedIn)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error checking user login status")
                _isLoggedIn.emit(false)
            }
        }
    }

    fun onTokenExpired() {
        viewModelScope.launch {
            Timber.d("Token expired event received")
            _isLoggedIn.emit(false)
            _tokenExpiredEvent.emit(Unit)
        }
    }
}