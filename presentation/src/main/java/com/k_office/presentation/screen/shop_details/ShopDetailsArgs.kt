package com.k_office.presentation.screen.shop_details

import com.k_office.domain.model.Shop
import com.k_office.presentation.base.utils.Args
import kotlinx.parcelize.Parcelize

@Parcelize
class ShopDetailsArgs(
    val shop: Shop
) : Args