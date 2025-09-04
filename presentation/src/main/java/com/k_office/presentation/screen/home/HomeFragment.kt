package com.k_office.presentation.screen.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.k_office.presentation.R
import com.k_office.presentation.base.fragment.BaseFragment
import com.k_office.presentation.base.utils.FragmentUtil
import com.k_office.presentation.base.utils.viewBinding
import com.k_office.presentation.databinding.FragmentHomeBinding
import com.k_office.presentation.screen.main.MainFragment
import com.k_office.presentation.screen.other.OtherFragment
import com.k_office.presentation.screen.scan_bonus.ScanBonusFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private val binding by viewBinding(FragmentHomeBinding::inflate)
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var mainFragment: MainFragment
    private lateinit var otherFragment: OtherFragment
    private lateinit var scanBonusFragment: ScanBonusFragment

    private var activeFragment: Fragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = binding.root

    override fun setupClicks() {
        super.setupClicks()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val currentFragment =
                        requireActivity().supportFragmentManager.findFragmentByTag(
                            FragmentUtil.getFragmentTag(this@HomeFragment)
                        )
                    if (requireActivity().supportFragmentManager.fragments.size == 1 || currentFragment == this@HomeFragment) {
                        requireActivity().finishAffinity()
                    } else {
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
            })

        mainFragment = MainFragment()
        otherFragment = OtherFragment()
        scanBonusFragment = ScanBonusFragment()

        binding.apply {
            activeFragment = FragmentUtil.hideShowOrAdd(
                null,
                mainFragment,
                childFragmentManager,
                R.id.nav_container
            )
            bottomNav.setOnItemSelectedListener { item ->
                val selectedFragment = when (item.itemId) {
                    R.id.nav_main -> mainFragment
                    R.id.nav_profile -> otherFragment
                    else -> null
                }
                if (selectedFragment != null && selectedFragment != activeFragment) {
                    if ((selectedFragment == mainFragment && activeFragment != mainFragment) || (selectedFragment == otherFragment && activeFragment != otherFragment)) {
                        childFragmentManager.popBackStack(
                            null,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        )
                    }

                    activeFragment = FragmentUtil.hideShowOrAdd(
                        activeFragment,
                        selectedFragment,
                        childFragmentManager,
                        R.id.nav_container
                    )
                }
                true
            }

            // When reselecting the same tab, pop that tab's back stack to root.
            bottomNav.setOnItemReselectedListener { item ->
                when (item.itemId) {
                    R.id.nav_main, R.id.nav_profile -> {
                        childFragmentManager.popBackStack(
                            null,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        )
                        // Ensure the root fragment of the tab is visible after pop
                        val target =
                            if (item.itemId == R.id.nav_main) mainFragment else otherFragment
                        if (activeFragment != target) {
                            activeFragment = FragmentUtil.hideShowOrAdd(
                                activeFragment,
                                target,
                                childFragmentManager,
                                R.id.nav_container
                            )
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

    override fun setupViewModelCallbacks() {
        super.setupViewModelCallbacks()

        lifecycleScope.launch {
            viewModel
                .uiTextMessage
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect(::showMessage)
        }
    }
}