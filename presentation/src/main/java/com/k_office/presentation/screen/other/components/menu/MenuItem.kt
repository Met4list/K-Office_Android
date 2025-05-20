package com.k_office.presentation.screen.other.components.menu

import androidx.annotation.DrawableRes

class MenuItem(
    val title: String,
    @DrawableRes val icon: Int? = null,
    val isFirstOption: Boolean = false,
    val isLastOption: Boolean = false,
    val onClick: () -> Unit = {}
)