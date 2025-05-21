package com.k_office.presentation.base.utils

import android.view.View
import androidx.compose.ui.graphics.Color

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

fun getColorForChar(char: Char): Color {
    return when (char.uppercaseChar()) {
        // Латиниця
        in 'A'..'D' -> Color(0xFFEF5350) // Червоний
        in 'E'..'H' -> Color(0xFFAB47BC) // Фіолетовий
        in 'I'..'L' -> Color(0xFF5C6BC0) // Синій
        in 'M'..'P' -> Color(0xFF29B6F6) // Голубий
        in 'Q'..'T' -> Color(0xFF66BB6A) // Зелений
        in 'U'..'Z' -> Color(0xFFFFCA28) // Жовтий

        // Кирилиця
        in 'А'..'Г' -> Color(0xFFEF5350) // Червоний
        in 'Ґ'..'Є' -> Color(0xFFAB47BC) // Фіолетовий
        in 'Ж'..'Й' -> Color(0xFF5C6BC0) // Синій
        in 'К'..'М' -> Color(0xFF29B6F6) // Голубий
        in 'Н'..'С' -> Color(0xFF66BB6A) // Зелений
        in 'Т'..'Ц' -> Color(0xFFFFCA28) // Жовтий
        in 'Ч'..'Я' -> Color(0xFFFF7043) // Помаранчевий

        else -> Color.Gray
    }
}

