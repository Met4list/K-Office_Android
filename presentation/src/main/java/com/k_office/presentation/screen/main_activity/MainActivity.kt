package com.k_office.presentation.screen.main_activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.k_office.domain.data_source.TokenDataSource
import com.k_office.presentation.R
import com.k_office.presentation.base.utils.FragmentUtil
import com.k_office.presentation.base.utils.viewBinding
import com.k_office.presentation.databinding.ActivityMainBinding
import com.k_office.presentation.screen.login.LoginFragment
import com.k_office.presentation.utils.UserDataServiceManager
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupFragment()
    }

    private fun setupFragment() {
        handleNavigation()

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

        registerTokenExpiredReceiver()
    }

    private fun handleNavigation() {
        lifecycleScope.launch {
            viewModel.isLoggedIn.collect {
                if (it) {
                    userDataServiceManager.startPeriodicWork(lifecycleScope, this@MainActivity)
                } else {
                    userDataServiceManager.stopPeriodicWork(this@MainActivity)
                }
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