package io.github.pknujsp.everyweather.core.ui.theme

import android.graphics.Color
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
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

fun ComponentActivity.setWindowStyle() {
    window.run {
        WindowCompat.setDecorFitsSystemWindows(this, false)
        WindowCompat.getInsetsController(this, decorView).run {
            setStatusBarContentColor(SystemBarContentColor.BLACK)
            setNavigationBarContentColor(SystemBarContentColor.BLACK)
        }
        statusBarColor = Color.TRANSPARENT
        navigationBarColor = Color.TRANSPARENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isStatusBarContrastEnforced = false
            isNavigationBarContrastEnforced = false
        }
    }
}