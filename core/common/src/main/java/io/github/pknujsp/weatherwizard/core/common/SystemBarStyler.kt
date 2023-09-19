package io.github.pknujsp.weatherwizard.core.common

import android.graphics.Color
import android.view.Window
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat


class SystemBarStyler(window: Window) : SystemBarController {
    enum class SystemBarColor {
        BLACK, WHITE, UNKNOWN
    }

    private val windowInsetsController: WindowInsetsControllerCompat

    init {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.apply {
            //setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            statusBarColor = Color.TRANSPARENT
            navigationBarColor = Color.TRANSPARENT
        }
        windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
    }


    override fun setStyle(statusBarColor: SystemBarColor, navBarColor: SystemBarColor) {
        println("Changing system bar color...")
        windowInsetsController.apply {
            isAppearanceLightStatusBars = statusBarColor == SystemBarColor.BLACK
            isAppearanceLightNavigationBars = navBarColor == SystemBarColor.BLACK
        }
    }

}

interface SystemBarController {
    fun setStyle(statusBarColor: SystemBarStyler.SystemBarColor, navBarColor: SystemBarStyler.SystemBarColor)

}