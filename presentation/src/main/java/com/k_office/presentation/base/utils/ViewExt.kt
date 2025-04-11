package com.k_office.presentation.base.utils

import android.view.View

fun View.updateVisibility(visible: Boolean?) {
    visibility = if (visible == true) View.VISIBLE else View.GONE
}

fun updateVisibility(visible: Boolean? = false, vararg view: View?) {
    view.forEach {
        it?.updateVisibility(visible)
    }
}

fun View.updateSoftVisibility(visible: Boolean?) {
    visibility = if (visible == true) View.VISIBLE else View.INVISIBLE
}

fun View.updateEnabled(enabled: Boolean?) {
    isEnabled = enabled == true
}

fun View.updateEnabledWithAlpha(enabled: Boolean?) {
    updateEnabled(enabled)
    updateAlpha(enabled)
}

fun updateEnableWithAlpha(enabled: Boolean? = false, vararg view: View?) {
    view.forEach {
        it?.updateEnabledWithAlpha(enabled)
    }
}

fun View.updateAlpha(enabled: Boolean?) {
    alpha = if (enabled == true) 1f else 0.3f
}