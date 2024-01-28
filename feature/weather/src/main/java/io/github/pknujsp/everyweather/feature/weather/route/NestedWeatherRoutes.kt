package io.github.pknujsp.everyweather.feature.weather.route

import io.github.pknujsp.everyweather.core.ui.NestedParentRoutes
import io.github.pknujsp.everyweather.core.ui.NestedRoutes
import io.github.pknujsp.everyweather.core.ui.theme.SystemBarContentColor

sealed interface NestedWeatherRoutes : NestedRoutes {
    val isDependOnNetwork: Boolean
    val systemBarContentColor: SystemBarContentColor

    companion object : NestedParentRoutes {
        override val route: String = "Weather"
        override val startDestination: NestedWeatherRoutes get() = Main
        override val routes: Array<NestedRoutes>
            get() = arrayOf(Main, DetailHourlyForecast, DetailDailyForecast, ComparisonHourlyForecast, ComparisonDailyForecast)
    }

    data object Main : NestedWeatherRoutes {
        override val route: String = "Main"
        override val isDependOnNetwork: Boolean = true
        override val systemBarContentColor: SystemBarContentColor = SystemBarContentColor.WHITE
    }

    data object DetailHourlyForecast : NestedWeatherRoutes {
        override val route: String = "DetailHourlyForecast"
        override val isDependOnNetwork: Boolean = false
        override val systemBarContentColor: SystemBarContentColor = SystemBarContentColor.BLACK
    }

    data object DetailDailyForecast : NestedWeatherRoutes {
        override val route: String = "DetailDailyForecast"
        override val isDependOnNetwork: Boolean = false
        override val systemBarContentColor: SystemBarContentColor = SystemBarContentColor.BLACK
    }

    data object ComparisonHourlyForecast : NestedWeatherRoutes {
        override val route: String = "ComparisonHourlyForecast"
        override val isDependOnNetwork: Boolean = true
        override val systemBarContentColor: SystemBarContentColor = SystemBarContentColor.BLACK
    }

    data object ComparisonDailyForecast : NestedWeatherRoutes {
        override val route: String = "ComparisonDailyForecast"
        override val isDependOnNetwork: Boolean = true
        override val systemBarContentColor: SystemBarContentColor = SystemBarContentColor.BLACK
    }
}