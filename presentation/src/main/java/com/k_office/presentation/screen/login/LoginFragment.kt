package com.k_office.presentation.screen.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.k_office.presentation.base.fragment.BaseFragment
import com.k_office.presentation.base.utils.setFragmentContent
import com.k_office.presentation.screen.login.components.LoginScreen
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
        LoginScreen(viewModel)
    }

    override fun setupViewModelCallbacks() {
        super.setupViewModelCallbacks()

        // viewLifecycleOwner: після переходу на OTP view знищується і колектори зупиняються
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authType
                .collect { event ->
                    event?.getContentIfNotHandled()?.let {
                        showMessage(it.message)
                        navigateSafely(
                            LoginFragmentDirections.actionLoginFragmentToOtpVerificationFragment(
                                VerifyOtpArgs(it.phone, it.type)
                            )
                        )
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoggedIn
                .collect {
                    if (it) {
                        navigateSafely(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiTextMessage
                .collect(::showMessage)
        }
    }
}