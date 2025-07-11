package com.k_office.presentation.screen.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.k_office.presentation.base.fragment.BaseFragment
import com.k_office.presentation.base.utils.setFragmentContent
import com.k_office.presentation.screen.settings.components.SettingsScreen

class SettingsFragment: BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = setFragmentContent {
        SettingsScreen()
    }
}