package com.k_office.presentation.screen.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.k_office.presentation.R
import com.k_office.presentation.base.fragment.BaseFragment
import com.k_office.presentation.base.utils.FragmentUtil
import com.k_office.presentation.base.utils.viewBinding
import com.k_office.presentation.databinding.FragmentHomeBinding
import com.k_office.presentation.screen.main.MainFragment
import com.k_office.presentation.screen.other.OtherFragment
import com.k_office.presentation.screen.scan_bonus.ScanBonusFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: BaseFragment() {

    private val binding by viewBinding(FragmentHomeBinding::inflate)

    private lateinit var mainFragment: MainFragment
    private lateinit var otherFragment: OtherFragment
    private lateinit var scanBonusFragment: ScanBonusFragment

    private var activeFragment: Fragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun setupClicks() {
        super.setupClicks()
        mainFragment = MainFragment()
        otherFragment = OtherFragment()
        scanBonusFragment = ScanBonusFragment()

        binding.apply {
            activeFragment = FragmentUtil.hideShowOrAdd(
                null,
                MainFragment(),
                childFragmentManager,
                R.id.nav_container
            )
            bottomNav.setOnItemSelectedListener { item ->
                val selectedFragment = when (item.itemId) {
                    R.id.nav_main -> mainFragment
                    R.id.nav_scan_bonus_card -> scanBonusFragment
                    R.id.nav_profile -> otherFragment
                    else -> null
                }
                if (selectedFragment != null && selectedFragment != activeFragment) {
                    activeFragment = FragmentUtil.hideShowOrAdd(
                        activeFragment,
                        selectedFragment,
                        childFragmentManager,
                        R.id.nav_container
                    )
                }
                true
            }
        }
    }
}