package com.k_office.presentation.screen.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.k_office.presentation.R
import com.k_office.presentation.base.fragment.BaseFragment
import com.k_office.presentation.base.utils.FragmentArgs
import com.k_office.presentation.base.utils.FragmentUtil
import com.k_office.presentation.base.utils.args
import com.k_office.presentation.base.utils.setFragmentContent
import com.k_office.presentation.screen.home.HomeFragment
import com.k_office.presentation.screen.registration.components.RegistrationScreen
import com.k_office.presentation.screen.main_activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegistrationFragment : BaseFragment(), FragmentArgs<RegistrationArgs> {

    private val viewModel: RegistrationViewModel by viewModels()

    private val args by args()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = setFragmentContent {

        RegistrationScreen(viewModel, args.phoneNumber)
    }

    override fun setupViewModelCallbacks() {
        super.setupViewModelCallbacks()

        lifecycleScope.launch {
            viewModel.isSuccessfulyRegistered.collect {
                if (it) {
                    showMessage(id = R.string.successfully_registered)
                    FragmentUtil.setFragmentIfAbsent(
                        HomeFragment(),
                        requireActivity() as MainActivity,
                        R.id.container
                    )
                }
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