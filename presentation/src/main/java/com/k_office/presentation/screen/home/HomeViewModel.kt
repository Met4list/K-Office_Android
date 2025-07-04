package com.k_office.presentation.screen.home

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.k_office.domain.data_source.CurrentUserInfoDataSource
import com.k_office.domain.model.AdsBanner
import com.k_office.domain.model.CurrentUserInfoModel
import com.k_office.domain.use_case.GetAdsBannersUseCase
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
    private val currentUserInfoDataSource: CurrentUserInfoDataSource,
    private val getAdsBannersUseCase: GetAdsBannersUseCase
) : BaseViewModel() {

    private val _currentUser = MutableStateFlow<CurrentUserInfoModel?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _logoutAction = MutableSharedFlow<Boolean>()
    val logoutAction = _logoutAction.asSharedFlow()

    private val _banners = MutableSharedFlow<List<AdsBanner>>()
    val banners = _banners.asSharedFlow()

    init {
        runCatching {
            viewModelScope.launch(coroutineExceptionHandler) {
                val user = currentUserInfoDataSource.getUser()
                if (user != null) {
                    _currentUser.emit(user)
                }
            }

        }.onFailure {
            Timber.e(it)
        }
    }

    fun logout() {
        viewModelScope.launch(coroutineExceptionHandler) {
            currentUserInfoDataSource.clear()
            _logoutAction.emit(true)
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