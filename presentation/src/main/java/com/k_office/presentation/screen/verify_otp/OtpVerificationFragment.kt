package com.k_office.presentation.screen.verify_otp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.k_office.domain.mapper.AuthType
import com.k_office.presentation.R
import com.k_office.presentation.base.fragment.BaseFragment
import com.k_office.presentation.base.utils.FragmentArgs
import com.k_office.presentation.base.utils.setFragmentContent
import com.k_office.presentation.screen.main_activity.MainActivity
import com.k_office.presentation.screen.verify_otp.args.VerifyOtpArgs
import com.k_office.presentation.screen.verify_otp.components.OtpVerificationScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OtpVerificationFragment : BaseFragment(), FragmentArgs<VerifyOtpArgs> {

    private val viewModel: OtpVerificationViewModel by viewModels()

    private lateinit var args: OtpVerificationFragmentArgs

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = setFragmentContent {
        args = OtpVerificationFragmentArgs.fromBundle(requireArguments())
        OtpVerificationScreen(
            viewModel = viewModel,
            phoneNumber = args.verifyOtpArgs.phoneNumber,
            onVerificationComplete = {
                val type = AuthType.findByType(args.verifyOtpArgs.type)
                if (type == AuthType.REGISTER) {
                    viewModel.verifyRegister(args.verifyOtpArgs.phoneNumber, it)
                } else {
                    viewModel.verifyOtp(args.verifyOtpArgs.phoneNumber, it)
                }
            }, onRetryClick = {
                viewModel.retryOtp(args.verifyOtpArgs.phoneNumber)
                viewModel.clearOTP()
            }
        )
    }

    override fun setupViewModelCallbacks() {
        super.setupViewModelCallbacks()
        lifecycleScope.launch {
            viewModel.onSuccess
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect {
                    when (AuthType.findByType(args.verifyOtpArgs.type)) {
                        AuthType.REGISTER -> {
                            if (it) {
                                navController.navigate(
                                    OtpVerificationFragmentDirections.actionOtpVerificationFragmentToRegistrationFragment(
                                        args.verifyOtpArgs.phoneNumber
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

                        else -> Unit
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