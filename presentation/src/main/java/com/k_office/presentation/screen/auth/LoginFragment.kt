package com.k_office.presentation.screen.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.k_office.presentation.R
import com.k_office.presentation.base.utils.FragmentUtil
import com.k_office.presentation.base.utils.setFragmentContent
import com.k_office.presentation.screen.auth.components.LoginScreen
import com.k_office.presentation.screen.login.RegistrationFragment
import com.k_office.presentation.screen.main_activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment: Fragment() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = setFragmentContent {
        LoginScreen(viewModel) {
            FragmentUtil.setFragmentIfAbsent(RegistrationFragment(), requireActivity().supportFragmentManager, R.id.container)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel
                .onSuccess
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect {
                    if (it) {
                        (requireActivity() as MainActivity).clearLogin(this@LoginFragment)
                    }
                }
        }
    }
}