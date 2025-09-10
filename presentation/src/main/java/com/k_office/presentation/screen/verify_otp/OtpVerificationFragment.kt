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
import com.k_office.presentation.base.utils.FragmentUtil
import com.k_office.presentation.base.utils.args
import com.k_office.presentation.base.utils.setArgs
import com.k_office.presentation.base.utils.setFragmentContent
import com.k_office.presentation.screen.home.HomeFragment
import com.k_office.presentation.screen.main_activity.MainActivity
import com.k_office.presentation.screen.registration.RegistrationArgs
import com.k_office.presentation.screen.registration.RegistrationFragment
import com.k_office.presentation.screen.verify_otp.args.VerifyOtpArgs
import com.k_office.presentation.screen.verify_otp.components.OtpVerificationScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OtpVerificationFragment : BaseFragment(), FragmentArgs<VerifyOtpArgs> {

    private val viewModel: OtpVerificationViewModel by viewModels()

    private val args by args()

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
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect {
                    when (AuthType.findByType(args.type)) {
                        AuthType.REGISTER -> {
                            if (it) {
                                FragmentUtil.setFragmentIfAbsent(
                                    RegistrationFragment().setArgs(RegistrationArgs(args.phoneNumber)),
                                    requireActivity() as MainActivity,
                                    R.id.container
                                )
                            }
                        }
                        AuthType.LOGIN -> {
                            if (it) {
                                showMessage(id = R.string.successfully_auth)
                                (requireActivity() as MainActivity).onUserUpdateStart()
                                FragmentUtil.setFragmentIfAbsent(
                                    HomeFragment(),
                                    requireActivity() as MainActivity,
                                    R.id.container
                                )
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