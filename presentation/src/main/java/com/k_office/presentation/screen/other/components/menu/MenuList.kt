package com.k_office.presentation.screen.other.components.menu

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.k_office.presentation.R
import com.k_office.presentation.base.utils.openBrowserPage

@Composable
internal inline fun MenuList() {

    val context = LocalContext.current

    val currentList = listOf(
        MenuItem(stringResource(R.string.news), isFirstOption = true),
        MenuItem(stringResource(R.string.we_on_map_title)),
        MenuItem(stringResource(R.string.settings)),
        MenuItem(stringResource(R.string.feedback)),
        MenuItem(stringResource(R.string.privacy_policy), onClick = {
            context.openBrowserPage(PRIVACY_POLICY_LINK)
        })
    )

    LazyColumn {
        items(currentList) { menu ->
            MenuOption(menu)
        }
    }
}

private const val PRIVACY_POLICY_LINK = "https://k-office.vn.ua/politika-konfidencijnosti"
private const val OFFER_AGREEMENT_LINK = "https://k-office.vn.ua/publichnij-dogovir-oferta"