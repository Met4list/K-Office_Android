package com.k_office.presentation.screen.scan_bonus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.k_office.presentation.base.fragment.BaseFragment
import com.k_office.presentation.base.utils.setFragmentContent
import com.k_office.presentation.screen.home.HomeViewModel
import com.k_office.presentation.screen.scan_bonus.components.ScanBonusScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanBonusFragment : BaseFragment() {

    private val viewModel: HomeViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = setFragmentContent {
        ScanBonusScreen(viewModel)
    }
}