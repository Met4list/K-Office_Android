package com.k_office.presentation.screen.other.components

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import com.k_office.presentation.base.compose.LoadingDialog
import com.k_office.presentation.screen.home.HomeViewModel
import com.k_office.presentation.screen.other.components.menu.MenuList

@Composable
internal fun OtherScreen(viewModel: HomeViewModel, fragmentManager: FragmentManager) {

    val context = LocalContext.current

    val currentUser = viewModel.currentUser.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val uiText by viewModel.uiTextMessage.collectAsState()


    LaunchedEffect(uiText) {
        if (uiText != null) {
            Toast.makeText(context, uiText?.getString(context), Toast.LENGTH_SHORT).show()
        }
    }

    if (loading) {
        LoadingDialog()
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderInfo(
                name = currentUser.value?.name.orEmpty(),
                phone = currentUser.value?.telephone.orEmpty()
            )
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            MenuList(viewModel, fragmentManager)
        }
    }
}