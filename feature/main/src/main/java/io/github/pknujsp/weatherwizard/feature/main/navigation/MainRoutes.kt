package io.github.pknujsp.weatherwizard.feature.main.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.github.pknujsp.core.annotation.KBindFunc
import io.github.pknujsp.weatherwizard.feature.main.R

@KBindFunc
sealed class MainRoutes(
    override val route: String, @DrawableRes val navIcon: Int, @StringRes val navTitle: Int
) : Routes(route) {

    data object Favorite :
        MainRoutes("favorite", io.github.pknujsp.weatherwizard.core.common.R.drawable.ic_baseline_map_24, R.string.area_info)

    data object Home : MainRoutes("home", io.github.pknujsp.weatherwizard.core.common.R.drawable.day_clear, R.string.weather_info)

    data object Settings :
        MainRoutes("settings", io.github.pknujsp.weatherwizard.core.common.R.drawable.baseline_settings_24, R.string.settings)
}