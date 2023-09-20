package io.github.pknujsp.weatherwizard.feature.main

import io.github.pknujsp.weatherwizard.core.ui.ParentRoutes

sealed interface MainRoutes : ParentRoutes {

    companion object : ParentRoutes {
        override val route: String = "Root"
        override val navIcon: Int = -1
        override val navTitle: Int = -1
    }

    data object Weather : MainRoutes {
        override val route: String = "Weather"
        override val navIcon: Int = io.github.pknujsp.weatherwizard.core.common.R.drawable.day_clear
        override val navTitle: Int = io.github.pknujsp.weatherwizard.core.common.R.string.nav_weather
    }

    data object Favorite : MainRoutes {
        override val route: String = "Favorite"
        override val navIcon: Int = io.github.pknujsp.weatherwizard.core.common.R.drawable.ic_baseline_map_24
        override val navTitle: Int = io.github.pknujsp.weatherwizard.core.common.R.string.nav_favorite_areas
    }

    data object Settings : MainRoutes {
        override val route: String = "Settings"
        override val navIcon: Int = io.github.pknujsp.weatherwizard.core.common.R.drawable.baseline_settings_24
        override val navTitle: Int = io.github.pknujsp.weatherwizard.core.common.R.string.nav_settings
    }
}