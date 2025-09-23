package com.k_office.presentation.screen.other.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.k_office.presentation.R
import com.k_office.presentation.base.compose.LoadingDialog
import com.k_office.presentation.base.utils.FragmentUtil
import com.k_office.presentation.screen.home.HomeViewModel
import com.k_office.presentation.screen.other.components.menu.MenuList
import com.k_office.presentation.screen.profile.ProfileFragment

@Composable
internal fun OtherScreen(viewModel: HomeViewModel, fragmentManager: FragmentManager) {

    val currentUser = viewModel.currentUser.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    if (loading) {
        LoadingDialog()
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderInfo(
                name = currentUser.value?.name.orEmpty(),
                phone = currentUser.value?.telephone.orEmpty()
            ) {
                FragmentUtil.setFragmentIfAbsent(ProfileFragment(), fragmentManager, R.id.nav_container)
            }
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            MenuList(viewModel, fragmentManager)
        }
    }
}