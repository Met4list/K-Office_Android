package com.k_office.presentation.screen.main_activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.k_office.domain.data_source.TokenDataSource
import com.k_office.presentation.R
import com.k_office.presentation.base.activity.BaseActivity
import com.k_office.presentation.base.utils.FragmentUtil
import com.k_office.presentation.screen.home.HomeFragment
import com.k_office.presentation.screen.login.LoginFragment
import com.k_office.presentation.utils.UserDataServiceManager
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override val layoutId: Int = R.layout.activity_main

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var tokenStorage: TokenDataSource

    @Inject
    lateinit var userDataServiceManager: UserDataServiceManager

    private val tokenExpiredReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "ACTION_TOKEN_EXPIRED" -> {
                    handleTokenExpired()
                }
            }
        }
    }

    override fun setupFragment() {
        super.setupFragment()
        registerTokenExpiredReceiver()

        lifecycleScope.launch {
            viewModel.isLoggedIn.collect {
                if (it) {
                    FragmentUtil.setFragmentIfAbsent(
                        HomeFragment(),
                        this@MainActivity,
                        R.id.container
                    )
                    // Start background service when user is logged in
                    userDataServiceManager.startPeriodicWork(lifecycleScope, this@MainActivity)
                } else {
                    FragmentUtil.setFragmentIfAbsent(
                        LoginFragment(),
                        this@MainActivity,
                        R.id.container
                    )
                    // Stop background service when user is logged out
                    userDataServiceManager.stopPeriodicWork(this@MainActivity)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uiTextMessage.collect { uIText ->
                if (uIText != null) {
                    Toast.makeText(
                        this@MainActivity,
                        uIText.getString(this@MainActivity),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.tokenExpiredEvent.collect {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

                FragmentUtil.setFragmentIfAbsent(
                    LoginFragment(),
                    this@MainActivity,
                    R.id.container
                )
            }
        }
    }

    fun logout() {
        userDataServiceManager.stopPeriodicWork(this)

        FragmentUtil.hideShowOrAdd(
            null,
            LoginFragment(),
            supportFragmentManager,
            R.id.container
        )
    }

    private fun registerTokenExpiredReceiver() {
        val filter = IntentFilter("ACTION_TOKEN_EXPIRED")
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(tokenExpiredReceiver, filter)
    }

    private fun handleTokenExpired() {
        lifecycleScope.launch {
            viewModel.onTokenExpired()
            userDataServiceManager.stopPeriodicWork(this@MainActivity)
        }
    }

    fun onUserUpdateStart() {
        userDataServiceManager.startPeriodicWork(lifecycleScope, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(tokenExpiredReceiver)
    }
}