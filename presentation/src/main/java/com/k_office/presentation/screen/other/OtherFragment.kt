package com.k_office.presentation.screen.other

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.k_office.presentation.base.fragment.BaseFragment
import com.k_office.presentation.base.utils.setFragmentContent
import com.k_office.presentation.screen.home.HomeViewModel
import com.k_office.presentation.screen.main_activity.MainActivity
import com.k_office.presentation.screen.other.components.OtherScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OtherFragment : BaseFragment() {

    private val viewModel: HomeViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = setFragmentContent {
        OtherScreen(viewModel)
    }

    override fun setupViewModelCallbacks() {
        super.setupViewModelCallbacks()

        lifecycleScope.launch {
            viewModel.logoutAction.collect {
                if (it) {
                    (requireActivity() as MainActivity).logout(this@OtherFragment)
                }
            }
        }
    }
}