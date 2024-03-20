package io.github.pknujsp.everyweather.feature.weather.route

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.route.ParentRoutes
import io.github.pknujsp.everyweather.core.ui.route.Routes

sealed interface WeatherRoutes : Routes {
    companion object : ParentRoutes {
        override val route: String = "Weather"

        @DrawableRes override val navIcon: Int = R.drawable.ic_weather_clear_day

        @StringRes override val navTitle: Int = R.string.nav_weather
    }

    data object Info : WeatherRoutes {
        override val route: String = "Weather/Info"
    }
}
