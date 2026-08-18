package com.k_office.presentation.screen.all_shops

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.k_office.presentation.base.fragment.BaseFragment
import com.k_office.presentation.base.utils.setFragmentContent
import com.k_office.presentation.screen.all_shops.components.AllShopsScreen
import com.k_office.presentation.screen.shop_list.ShopListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllShopsFragment: BaseFragment() {

    private val viewModel: ShopListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = setFragmentContent {
        AllShopsScreen(viewModel)
    }

    override fun setupViewModelCallbacks() {
        super.setupViewModelCallbacks()

        lifecycleScope.launch {
            viewModel
                .uiTextMessage
                .collect(::showMessage)
        }
    }
}