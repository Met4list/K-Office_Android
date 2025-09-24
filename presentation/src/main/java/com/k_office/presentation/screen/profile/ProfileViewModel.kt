package com.k_office.presentation.screen.profile

import com.k_office.domain.model.CurrentUserModel
import com.k_office.domain.model.UpdateUserData
import com.k_office.domain.use_case.EditCurrentUserUseCase
import com.k_office.domain.use_case.GetCurrentUserUseCase
import com.k_office.presentation.base.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val editCurrentUserUseCase: EditCurrentUserUseCase
): BaseViewModel() {

    private val _currentUser = MutableStateFlow<CurrentUserModel?>(null)
    val currentUser = _currentUser.asSharedFlow()

    private val _successfullyEdited = MutableStateFlow(false)
    val successfullyEdited = _successfullyEdited.asStateFlow()

    private val _successfullyMessage = MutableStateFlow("")
    val successfullyMessage = _successfullyMessage.asStateFlow()

    init {
        launchWithResponseState(block = { getCurrentUserUseCase.invoke(Unit) }) {
            _currentUser.emit(it)
        }
    }

    fun updateUser(phoneNumber: String, name: String) {
        launchWithResponseState(block = { editCurrentUserUseCase.invoke(UpdateUserData(
            name, phoneNumber
        )) }) {
            it?.let { (message, userModel) ->
                _currentUser.emit(userModel)
                _successfullyMessage.emit(message)
                _successfullyEdited.emit(true)
            }
        }
    }
}