package com.k_office.presentation.screen.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
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

        lifecycleScope.launch {
            viewModel.authType
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect { event ->
                    event?.getContentIfNotHandled()?.let {
                        showMessage(it.message)
                        navController.navigate(
                            LoginFragmentDirections.actionLoginFragmentToOtpVerificationFragment(
                                VerifyOtpArgs(it.phone, it.type)
                            )
                        )
                    }
                }
        }

        lifecycleScope.launch {
            viewModel.isLoggedIn
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect {
                    if (it) {
                        navController.navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                    }
                }
        }

        lifecycleScope.launch {
            viewModel.uiTextMessage
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect(::showMessage)
        }
    }
}