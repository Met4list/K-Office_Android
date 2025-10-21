package com.k_office.presentation.screen.shop_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.k_office.presentation.R
import com.k_office.presentation.base.fragment.BaseFragment
import com.k_office.presentation.base.utils.FragmentUtil
import com.k_office.presentation.base.utils.setArgs
import com.k_office.presentation.base.utils.setFragmentContent
import com.k_office.presentation.screen.shop_details.ShopDetailsArgs
import com.k_office.presentation.screen.shop_details.ShopDetailsFragment
import com.k_office.presentation.screen.shop_list.components.ShopListScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShopListFragment : BaseFragment() {

    private val viewModel: ShopListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = setFragmentContent {
        viewModel.loadShops(requireContext())

        ShopListScreen(viewModel)
    }

    override fun setupViewModelCallbacks() {
        super.setupViewModelCallbacks()

        lifecycleScope.launch {
            viewModel
                .uiTextMessage
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect(::showMessage)
        }

        lifecycleScope.launch {
            viewModel
                .shopDetails
                .collect { shop ->
                    if (shop != null) {
                        FragmentUtil.setFragmentIfAbsent(
                            ShopDetailsFragment().setArgs(ShopDetailsArgs(shop)),
                            parentFragmentManager,
                            R.id.nav_container
                        )
                    }
                }
        }
    }
}