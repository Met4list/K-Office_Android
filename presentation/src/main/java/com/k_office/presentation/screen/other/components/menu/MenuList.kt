package com.k_office.presentation.screen.other.components.menu

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentManager
import com.k_office.presentation.R
import com.k_office.presentation.base.compose.ConfirmationDialog
import com.k_office.presentation.base.utils.FragmentUtil
import com.k_office.presentation.base.utils.findActivity
import com.k_office.presentation.base.utils.openBrowserPage
import com.k_office.presentation.screen.all_shops.AllShopsFragment
import com.k_office.presentation.screen.home.HomeViewModel
import com.k_office.presentation.screen.settings.SettingsFragment

@Composable
internal inline fun MenuList(
    viewModel: HomeViewModel,
    fragmentManager: FragmentManager
) {

    val context = LocalContext.current

    var showLogoutDialog by remember {
        mutableStateOf(false)
    }

    val currentList = listOf(
//        MenuItem(stringResource(R.string.news), isFirstOption = true),
        MenuItem(stringResource(R.string.we_on_map_title), onClick = {
            FragmentUtil.setFragmentIfAbsent(AllShopsFragment(), fragmentManager, R.id.nav_container)
        }),
//        MenuItem(stringResource(R.string.settings), onClick = {
//            FragmentUtil.setFragmentIfAbsent(SettingsFragment(), context.findActivity(), R.id.nav_container)
//        }),
        MenuItem(stringResource(R.string.feedback), onClick = {
            context.openBrowserPage(FEEDBACK_LINK)
        }),
        MenuItem(stringResource(R.string.privacy_policy), onClick = {
            context.openBrowserPage(PRIVACY_POLICY_LINK)
        }),
        MenuItem(
            stringResource(R.string.logout),
            onClick = {
                showLogoutDialog = true
            }
        )
    )

    if (showLogoutDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.logout),
            message = stringResource(R.string.logout_message),
            confirmText = stringResource(R.string.dialog_button_exit),
            dismissText = stringResource(R.string.dialog_button_cancel),
            onConfirm = {
                showLogoutDialog = false
                viewModel.logout()
            },
            onCancel = {
                showLogoutDialog = false
            }
        )
    }

    LazyColumn {
        items(currentList) { menu ->
            MenuOption(menu)
        }
    }
}

private const val PRIVACY_POLICY_LINK = "https://k-office.vn.ua/politika-konfidencijnosti"
private const val OFFER_AGREEMENT_LINK = "https://k-office.vn.ua/publichnij-dogovir-oferta"
private const val FEEDBACK_LINK = "https://k-office.vn.ua/reviews"