package io.github.pknujsp.weatherwizard.feature.weather.route

import io.github.pknujsp.weatherwizard.core.ui.NestedParentRoutes
import io.github.pknujsp.weatherwizard.core.ui.NestedRoutes

sealed interface NestedWeatherRoutes : NestedRoutes {
    companion object : NestedParentRoutes {
        override val route: String = "Weather"
        override val startDestination: NestedRoutes get() = Main
        override val routes: Array<NestedRoutes>
            get() = arrayOf(Main, DetailHourlyForecast, DetailDailyForecast,
                ComparisonHourlyForecast, ComparisonDailyForecast)

    }

    data object Main : NestedWeatherRoutes {
        override val route: String = "Main"
    }

    data object DetailHourlyForecast : NestedWeatherRoutes {
        override val route: String = "DetailHourlyForecast"
    }

    data object DetailDailyForecast : NestedWeatherRoutes {
        override val route: String = "DetailDailyForecast"
    }

    data object ComparisonHourlyForecast : NestedWeatherRoutes {
        override val route: String = "ComparisonHourlyForecast"
    }

    data object ComparisonDailyForecast : NestedWeatherRoutes {
        override val route: String = "ComparisonDailyForecast"
    }
}