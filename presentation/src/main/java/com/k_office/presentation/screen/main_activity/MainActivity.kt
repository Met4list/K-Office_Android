package com.k_office.presentation.screen.main_activity

import com.k_office.presentation.R
import com.k_office.presentation.base.activity.BaseActivity
import com.k_office.presentation.base.utils.FragmentUtil
import com.k_office.presentation.screen.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override val layoutId: Int = R.layout.activity_main

    override fun setupFragment() {
        super.setupFragment()
        FragmentUtil.setFragmentIfAbsent(HomeFragment(), this, R.id.container)
    }
}