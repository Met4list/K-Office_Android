package com.k_office.presentation.screen.home

import android.content.Context
import com.k_office.domain.model.AdsBanner
import com.k_office.domain.model.CurrentUserModel
import com.k_office.domain.use_case.GetAdsBannersUseCase
import com.k_office.domain.use_case.GetCurrentUserUseCase
import com.k_office.domain.use_case.LogoutUseCase
import com.k_office.domain.use_case.UpdateUserInfoUseCase
import com.k_office.presentation.base.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getAdsBannersUseCase: GetAdsBannersUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val updateUserInfoUseCase: UpdateUserInfoUseCase
) : BaseViewModel() {

    private val _currentUser = MutableStateFlow<CurrentUserModel?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _logoutAction = MutableStateFlow(false)
    val logoutAction = _logoutAction.asStateFlow()

    private val _banners = MutableSharedFlow<List<AdsBanner>>()
    val banners = _banners.asSharedFlow()

    init {
        launchWithResponseState(block = { getCurrentUserUseCase.invoke(Unit) }) {
            _currentUser.emit(it)
        }
    }

    fun logout() {
        launchWithResponseState(block = { logoutUseCase.invoke(Unit) }) {
            _logoutAction.emit(it)
        }
    }

    fun loadBanners(context: Context) {
        launchWithResponseState(block = { getAdsBannersUseCase.invoke(context) }) {
            _banners.emit(it)
        }
    }

    fun updateUserInfo() {
        launchWithResponseState(block = { updateUserInfoUseCase.invoke(Unit) }) {
            _currentUser.emit(it)
        }
    }
}