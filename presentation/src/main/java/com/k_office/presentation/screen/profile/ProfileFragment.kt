package com.k_office.presentation.screen.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.k_office.presentation.base.fragment.BaseFragment
import com.k_office.presentation.base.utils.setFragmentContent
import com.k_office.presentation.screen.profile.components.ProfileScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment: BaseFragment() {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = setFragmentContent {
        ProfileScreen(viewModel)
    }

    override fun setupViewModelCallbacks() {
        super.setupViewModelCallbacks()

        lifecycleScope.launch {
            viewModel
                .successfullyEdited
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect {
                    if (it) parentFragmentManager.popBackStack()
                }
        }

        lifecycleScope.launch {
            viewModel
                .uiTextMessage
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect(::showMessage)
        }
    }
}