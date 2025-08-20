package com.k_office.presentation.screen.main_activity

import androidx.lifecycle.viewModelScope
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
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : BaseViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    init {
        checkUserLoggedIn()
    }

    private fun checkUserLoggedIn() {
        runCatching {
            viewModelScope.launch(coroutineExceptionHandler) {
                val currentUser = getCurrentUserUseCase.invoke(Unit)
                _isLoggedIn.emit(currentUser != null)
            }
        }.onFailure {
            Timber.e(it)
        }
    }
}