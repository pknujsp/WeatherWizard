package io.github.pknujsp.everyweather.core.ui.theme

import androidx.core.view.WindowInsetsControllerCompat

fun WindowInsetsControllerCompat.setStatusBarContentColor(color: SystemBarContentColor) {
    isAppearanceLightStatusBars = color.isLight
}

fun WindowInsetsControllerCompat.setNavigationBarContentColor(color: SystemBarContentColor) {
    isAppearanceLightNavigationBars = color.isLight
}

enum class SystemBarContentColor(val isLight: Boolean) {
    WHITE(false), BLACK(true)
}