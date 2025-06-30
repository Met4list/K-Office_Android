package com.k_office.presentation.screen.home

import androidx.lifecycle.viewModelScope
import com.k_office.domain.data_source.CurrentUserInfoDataSource
import com.k_office.domain.model.CurrentUserInfoModel
import com.k_office.domain.use_case.GetShopsInfoUseCase
import com.k_office.presentation.base.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val currentUserInfoDataSource: CurrentUserInfoDataSource,

) : BaseViewModel() {

    private val _currentUser = MutableStateFlow<CurrentUserInfoModel?>(null)
    val currentUser = _currentUser.asStateFlow()

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
}