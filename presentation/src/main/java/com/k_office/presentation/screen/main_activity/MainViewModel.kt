package com.k_office.presentation.screen.main_activity

import androidx.lifecycle.viewModelScope
import com.k_office.domain.data_source.TokenDataSource
import com.k_office.domain.use_case.GetCurrentUserUseCase
import com.k_office.presentation.base.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val tokenDataSource: TokenDataSource
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
                val accessToken = tokenDataSource.receiveTokens()?.accessToken
                val refreshToken = tokenDataSource.receiveTokens()?.refreshToken

                if (accessToken == null || refreshToken == null) {
                    Timber.d("No tokens found, user not logged in")
                    _isLoggedIn.emit(false)
                    return@launch
                }

                launchWithResponseState(block = { getCurrentUserUseCase.invoke(Unit) }) { currentUser ->
                    val isLoggedIn = currentUser != null
                    Timber.d("Current user check result: $isLoggedIn")
                    _isLoggedIn.emit(isLoggedIn)

                    if (!isLoggedIn) {
                        clearTokens()
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error checking user login status")
                _isLoggedIn.emit(false)
                clearTokens()
            }
        }
    }

    fun onTokenExpired() {
        viewModelScope.launch {
            Timber.d("Token expired event received")
            clearTokens()
            _isLoggedIn.emit(false)
            _tokenExpiredEvent.emit(Unit)
        }
    }

    fun logout() {
        viewModelScope.launch {
            clearTokens()
            _isLoggedIn.emit(false)
        }
    }

    private suspend fun clearTokens() {
        try {
            tokenDataSource.clear()
            Timber.d("Tokens cleared")
        } catch (e: Exception) {
            Timber.e(e, "Error clearing tokens")
        }
    }
}