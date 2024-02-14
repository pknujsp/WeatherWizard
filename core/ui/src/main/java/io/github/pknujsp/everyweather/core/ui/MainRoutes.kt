package io.github.pknujsp.everyweather.core.ui

import io.github.pknujsp.everyweather.core.ui.route.ParentRoutes
import io.github.pknujsp.everyweather.core.ui.theme.SystemBarContentColor

sealed interface MainRoutes : ParentRoutes {
    val systemBarContentColor: SystemBarContentColor

    companion object : ParentRoutes {
        override val route: String = "Root"
        override val navIcon: Int = -1
        override val navTitle: Int = -1
    }

    data object Weather : MainRoutes {
        override val route: String = "Weather"
        override val navIcon: Int = io.github.pknujsp.everyweather.core.resource.R.drawable.ic_weather_clear_day
        override val navTitle: Int = io.github.pknujsp.everyweather.core.resource.R.string.nav_weather
        override val systemBarContentColor = SystemBarContentColor.BLACK
    }

    data object Favorite : MainRoutes {
        override val route: String = "Favorite"
        override val navIcon: Int = io.github.pknujsp.everyweather.core.resource.R.drawable.ic_baseline_map_24
        override val navTitle: Int = io.github.pknujsp.everyweather.core.resource.R.string.nav_favorite_areas
        override val systemBarContentColor = SystemBarContentColor.BLACK
    }

    data object Notification : MainRoutes {
        override val route: String = "Notification"
        override val navIcon: Int = io.github.pknujsp.everyweather.core.resource.R.drawable.round_notifications_24
        override val navTitle: Int = io.github.pknujsp.everyweather.core.resource.R.string.nav_notification
        override val systemBarContentColor = SystemBarContentColor.BLACK
    }

    data object Settings : MainRoutes {
        override val route: String = "Settings"
        override val navIcon: Int = io.github.pknujsp.everyweather.core.resource.R.drawable.baseline_settings_24
        override val navTitle: Int = io.github.pknujsp.everyweather.core.resource.R.string.nav_settings
        override val systemBarContentColor = SystemBarContentColor.BLACK
    }

    data object Onboarding : MainRoutes {
        override val route: String = "Onboarding"
        override val navIcon: Int = io.github.pknujsp.everyweather.core.resource.R.drawable.baseline_settings_24
        override val navTitle: Int = io.github.pknujsp.everyweather.core.resource.R.string.nav_onboarding
        override val systemBarContentColor = SystemBarContentColor.BLACK
    }
}