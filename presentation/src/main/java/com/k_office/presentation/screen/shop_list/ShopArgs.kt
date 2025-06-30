package com.k_office.presentation.screen.shop_list

import com.k_office.domain.model.Shop
import com.k_office.presentation.base.utils.Args
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShopArgs(
    val shops: List<Shop>
): Args