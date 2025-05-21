package com.k_office.presentation.screen.other.components.menu

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.k_office.presentation.R

@Composable
inline fun MenuList() {

    val currentList = listOf(
        MenuItem(stringResource(R.string.news), isFirstOption = true),
        MenuItem(stringResource(R.string.we_on_map_title)),
        MenuItem(stringResource(R.string.settings)),
        MenuItem(stringResource(R.string.feedback)),
        MenuItem(stringResource(R.string.privacy_policy))
    )

    LazyColumn {
        items(currentList) { menu ->
            MenuOption(menu)
        }
    }
}