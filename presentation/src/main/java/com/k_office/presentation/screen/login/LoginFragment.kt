package com.k_office.presentation.screen.login

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
import com.k_office.presentation.base.utils.FragmentUtil
import com.k_office.presentation.base.utils.setArgs
import com.k_office.presentation.base.utils.setFragmentContent
import com.k_office.presentation.screen.login.components.LoginScreen
import com.k_office.presentation.screen.registration.RegistrationFragment
import com.k_office.presentation.screen.verify_otp.OtpVerificationFragment
import com.k_office.presentation.screen.verify_otp.args.VerifyOtpArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = setFragmentContent {
        LoginScreen(viewModel) {
            FragmentUtil.setFragmentIfAbsent(
                RegistrationFragment(),
                requireActivity().supportFragmentManager,
                R.id.container
            )
        }
    }

    override fun setupViewModelCallbacks() {
        super.setupViewModelCallbacks()

        lifecycleScope.launch {
            viewModel.phoneNumber
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect {
                    if (it.isNotEmpty()) {
                        FragmentUtil.setFragmentIfAbsent(
                            OtpVerificationFragment().setArgs(VerifyOtpArgs(it)),
                            requireActivity().supportFragmentManager,
                            R.id.container
                        )
                    }
                }
        }

        lifecycleScope.launch {
            viewModel.uiTextMessage
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect {
                    if (it != null) {
                        Toast.makeText(
                            requireContext(),
                            it.getString(requireContext()),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}