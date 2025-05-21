package com.k_office.presentation.screen.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.k_office.presentation.base.fragment.BaseFragment
import com.k_office.presentation.base.utils.setFragmentContent
import com.k_office.presentation.screen.login.components.RegistrationScreen
import com.k_office.presentation.screen.main_activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegistrationFragment : BaseFragment() {

    private val viewModel: RegistrationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = setFragmentContent {
        RegistrationScreen(viewModel) {
            (requireActivity() as MainActivity).clearLogin(this)
        }
    }

    override fun setupViewModelCallbacks() {
        super.setupViewModelCallbacks()
        lifecycleScope.launch {
            viewModel.isSuccessfulyRegistered.collect {
                if (it) {
                    (requireActivity() as MainActivity).clearLogin(this@RegistrationFragment)
                }
            }
        }
    }
}