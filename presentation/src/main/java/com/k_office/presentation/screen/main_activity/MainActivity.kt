package com.k_office.presentation.screen.main_activity

import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.k_office.presentation.R
import com.k_office.presentation.base.activity.BaseActivity
import com.k_office.presentation.base.utils.FragmentUtil
import com.k_office.presentation.screen.auth.LoginFragment
import com.k_office.presentation.screen.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override val layoutId: Int = R.layout.activity_main

    private val viewModel: MainViewModel by viewModels()

    override fun setupFragment() {
        super.setupFragment()

        lifecycleScope.launch {
            viewModel.isLoggedIn.collect {
                if (it) {
                    FragmentUtil.setFragmentIfAbsent(
                        HomeFragment(),
                        this@MainActivity,
                        R.id.container
                    )
                } else {
                    FragmentUtil.setFragmentIfAbsent(
                        LoginFragment(),
                        this@MainActivity,
                        R.id.container
                    )
                }
            }
        }

    }

    fun clearLogin(oldFragment: Fragment?) {
        FragmentUtil.hideShowOrAdd(
            oldFragment,
            HomeFragment(),
            supportFragmentManager,
            R.id.container
        )
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}