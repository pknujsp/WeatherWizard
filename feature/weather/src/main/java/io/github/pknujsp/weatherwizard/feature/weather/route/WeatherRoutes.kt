package io.github.pknujsp.weatherwizard.feature.weather.route

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.ParentRoutes
import io.github.pknujsp.weatherwizard.core.ui.Routes

sealed interface WeatherRoutes : Routes {

    companion object : ParentRoutes {
        override val route: String = "Weather"
        @DrawableRes override val navIcon: Int = R.drawable.ic_weather_clear_day
        @StringRes override val navTitle: Int = R.string.nav_weather
    }

    data object Main : WeatherRoutes {
        override val route: String = "Weather/Main"
    }

    data object Info : WeatherRoutes {
        override val route: String = "Weather/Info"
    }
}