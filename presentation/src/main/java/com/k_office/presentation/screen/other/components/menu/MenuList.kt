package com.k_office.presentation.screen.other.components.menu

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable

@Composable
inline fun MenuList() {

    val currentList = listOf(
        MenuItem("Новини", isFirstOption = true),
        MenuItem("Ми на мапі"),
        MenuItem("Налаштування"),
        MenuItem("Зворотній зв'язок"),
        MenuItem("Політика конфіденційності"),
        MenuItem("Про розробника", isLastOption = true)
    )

    LazyColumn {
        items(currentList) { menu ->
            MenuOption(menu)
        }
    }
}