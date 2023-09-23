package io.github.pknujsp.weatherwizard.feature.weather

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.github.pknujsp.weatherwizard.core.common.R
import io.github.pknujsp.weatherwizard.core.ui.ParentRoutes
import io.github.pknujsp.weatherwizard.core.ui.Routes

sealed interface WeatherRoutes : Routes {

    companion object : ParentRoutes {
        override val route: String = "Weather"
        @DrawableRes override val navIcon: Int = R.drawable.day_clear
        @StringRes override val navTitle: Int = io.github.pknujsp.weatherwizard.core.common.R.string.nav_weather
    }

    data object Main : WeatherRoutes {
        override val route: String = "Weather/Main"
    }

    data object Info : WeatherRoutes {
        override val route: String = "Weather/Info"
    }
}