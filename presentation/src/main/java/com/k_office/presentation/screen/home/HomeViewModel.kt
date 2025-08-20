package com.k_office.presentation.screen.home

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.k_office.domain.model.AdsBanner
import com.k_office.domain.model.CurrentUserModel
import com.k_office.domain.use_case.GetAdsBannersUseCase
import com.k_office.domain.use_case.GetCurrentUserUseCase
import com.k_office.domain.use_case.LogoutUseCase
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
class HomeViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getAdsBannersUseCase: GetAdsBannersUseCase,
    private val logoutUseCase: LogoutUseCase,
) : BaseViewModel() {

    private val _currentUser = MutableStateFlow<CurrentUserModel?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _logoutAction = MutableStateFlow(false)
    val logoutAction = _logoutAction.asStateFlow()

    private val _banners = MutableSharedFlow<List<AdsBanner>>()
    val banners = _banners.asSharedFlow()

    init {
        runCatching {
            viewModelScope.launch(coroutineExceptionHandler) {
                _currentUser.emit(getCurrentUserUseCase.invoke(Unit))
            }

        }.onFailure {
            Timber.e(it)
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            logoutUseCase {
                _logoutAction.value = true
            }
        }
    }

    fun loadBanners(context: Context) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            getAdsBannersUseCase.invoke(context).apply {
                _banners.emit(this)
            }
        }
    }
}