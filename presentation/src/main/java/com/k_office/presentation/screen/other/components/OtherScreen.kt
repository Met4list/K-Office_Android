package com.k_office.presentation.screen.other.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.k_office.presentation.screen.home.HomeViewModel
import com.k_office.presentation.screen.other.components.menu.MenuList

@Composable
internal fun OtherScreen(viewModel: HomeViewModel) {

    val currentUser = viewModel.currentUser.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        HeaderInfo(name = currentUser.value?.name.orEmpty(), phone = currentUser.value?.telephone.orEmpty())
        Spacer(modifier = Modifier.padding(vertical = 8.dp))
        MenuList()
    }
}