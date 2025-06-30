package com.k_office.presentation.screen.shop_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.k_office.presentation.base.utils.setFragmentContent
import com.k_office.presentation.screen.shop_list.components.ShopListScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShopListFragment: Fragment() {

    private val viewModel: ShopListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = setFragmentContent {
        viewModel.loadShops(requireContext())

        ShopListScreen(viewModel)
    }
}