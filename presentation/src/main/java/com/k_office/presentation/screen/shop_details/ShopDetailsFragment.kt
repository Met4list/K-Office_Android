package com.k_office.presentation.screen.shop_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.k_office.presentation.base.fragment.BaseFragment
import com.k_office.presentation.base.utils.FragmentArgs
import com.k_office.presentation.base.utils.args
import com.k_office.presentation.base.utils.setFragmentContent
import com.k_office.presentation.screen.shop_details.components.ShopDetailsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShopDetailsFragment : BaseFragment(), FragmentArgs<ShopDetailsArgs> {

    private val shopDetails by args()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = setFragmentContent {
        ShopDetailsScreen(shop = shopDetails.shop)
    }
}