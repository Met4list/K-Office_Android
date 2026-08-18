package com.k_office.presentation.screen.verify_otp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.k_office.domain.mapper.AuthType
import com.k_office.presentation.R
import com.k_office.presentation.base.fragment.BaseFragment
import com.k_office.presentation.base.utils.setFragmentContent
import com.k_office.presentation.screen.main_activity.MainActivity
import com.k_office.presentation.screen.verify_otp.components.OtpVerificationScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OtpVerificationFragment : BaseFragment() {

    private val viewModel: OtpVerificationViewModel by viewModels()

    private val args by lazy {
        OtpVerificationFragmentArgs.fromBundle(requireArguments()).verifyOtpArgs
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = setFragmentContent {
        OtpVerificationScreen(
            viewModel = viewModel,
            phoneNumber = args.phoneNumber,
            onVerificationComplete = {
                val type = AuthType.findByType(args.type)
                if (type == AuthType.REGISTER) {
                    viewModel.verifyRegister(args.phoneNumber, it)
                } else {
                    viewModel.verifyOtp(args.phoneNumber, it)
                }
            }, onRetryClick = {
                viewModel.retryOtp(args.phoneNumber)
                viewModel.clearOTP()
            }
        )
    }

    override fun setupViewModelCallbacks() {
        super.setupViewModelCallbacks()
        lifecycleScope.launch {
            viewModel.onSuccess
                .collect {
                    when (AuthType.findByType(args.type)) {
                        AuthType.REGISTER -> {
                            if (it) {
                                navController.navigate(
                                    OtpVerificationFragmentDirections.actionOtpVerificationFragmentToRegistrationFragment(
                                        args.phoneNumber
                                    )
                                )
                            }
                        }

                        AuthType.LOGIN -> {
                            if (it) {
                                showMessage(id = R.string.successfully_auth)
                                (requireActivity() as MainActivity).onUserUpdateStart()
                                navController.navigate(OtpVerificationFragmentDirections.actionOtpVerificationFragmentToHomeFragment())
                            }
                        }

                        else -> showMessage("Something went wrong.")
                    }
                }
        }

        lifecycleScope.launch {
            viewModel
                .uiTextMessage
                .collect(::showMessage)
        }
    }
}