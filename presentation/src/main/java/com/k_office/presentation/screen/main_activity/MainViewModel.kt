package com.k_office.presentation.screen.main_activity

import androidx.lifecycle.viewModelScope
import com.k_office.domain.data_source.CurrentUserInfoDataSource
import com.k_office.presentation.base.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val currentUserInfoDataSource: CurrentUserInfoDataSource
): BaseViewModel() {

    private val _isLoggedIn = MutableSharedFlow<Boolean>()
    val isLoggedIn = _isLoggedIn.asSharedFlow()

    init {
        checkUserLoggedIn()
    }

    private fun checkUserLoggedIn() {
        runCatching {
            viewModelScope.launch(coroutineExceptionHandler) {
                val currentUser = currentUserInfoDataSource.getUser()
                if (currentUser == null) _isLoggedIn.emit(false) else _isLoggedIn.emit(true)
            }
        }.onFailure {
            Timber.e(it)
        }
    }
}