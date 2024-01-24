package io.github.pknujsp.weatherwizard.feature.weather.route

import io.github.pknujsp.weatherwizard.core.ui.NestedParentRoutes
import io.github.pknujsp.weatherwizard.core.ui.NestedRoutes

sealed interface NestedWeatherRoutes : NestedRoutes {
    val isDependOnNetwork: Boolean

    companion object : NestedParentRoutes {
        override val route: String = "Weather"
        override val startDestination: NestedWeatherRoutes get() = Main
        override val routes: Array<NestedRoutes>
            get() = arrayOf(Main, DetailHourlyForecast, DetailDailyForecast, ComparisonHourlyForecast, ComparisonDailyForecast)

    }

    data object Main : NestedWeatherRoutes {
        override val route: String = "Main"
        override val isDependOnNetwork: Boolean = true
    }

    data object DetailHourlyForecast : NestedWeatherRoutes {
        override val route: String = "DetailHourlyForecast"
        override val isDependOnNetwork: Boolean = false
    }

    data object DetailDailyForecast : NestedWeatherRoutes {
        override val route: String = "DetailDailyForecast"
        override val isDependOnNetwork: Boolean = false
    }

    data object ComparisonHourlyForecast : NestedWeatherRoutes {
        override val route: String = "ComparisonHourlyForecast"
        override val isDependOnNetwork: Boolean = true

    }

    data object ComparisonDailyForecast : NestedWeatherRoutes {
        override val route: String = "ComparisonDailyForecast"
        override val isDependOnNetwork: Boolean = true
    }
}