package com.k_office.presentation.screen.choose_shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.k_office.presentation.base.utils.setFragmentContent
import com.k_office.presentation.screen.choose_shop.components.ChooseShopScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseShopFragment: Fragment() {

    private val viewModel: ChooseShopViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = setFragmentContent {
        ChooseShopScreen(viewModel)
    }
}