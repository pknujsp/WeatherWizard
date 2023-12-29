package io.github.pknujsp.weatherwizard.core.ui

/**
 * @property isAppearanceLightSystemBars true: 검정, false: 하양
 */
sealed interface MainRoutes : ParentRoutes {
    val isFullScreen: Boolean
    val isAppearanceLightSystemBars: Boolean

    companion object : ParentRoutes {
        override val route: String = "Root"
        override val navIcon: Int = -1
        override val navTitle: Int = -1
    }

    data object Weather : MainRoutes {
        override val route: String = "Weather"
        override val navIcon: Int = io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_weather_clear
        override val navTitle: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.nav_weather
        override val isFullScreen: Boolean = true
        override val isAppearanceLightSystemBars: Boolean = true
    }

    data object Favorite : MainRoutes {
        override val route: String = "Favorite"
        override val navIcon: Int = io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_baseline_map_24
        override val navTitle: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.nav_favorite_areas
        override val isFullScreen: Boolean = false
        override val isAppearanceLightSystemBars: Boolean = true
    }

    data object Notification : MainRoutes {
        override val route: String = "Notification"
        override val navIcon: Int = io.github.pknujsp.weatherwizard.core.resource.R.drawable.round_notifications_24
        override val navTitle: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.nav_notification
        override val isFullScreen: Boolean = false
        override val isAppearanceLightSystemBars: Boolean = true
    }

    data object Settings : MainRoutes {
        override val route: String = "Settings"
        override val navIcon: Int = io.github.pknujsp.weatherwizard.core.resource.R.drawable.baseline_settings_24
        override val navTitle: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.nav_settings
        override val isFullScreen: Boolean = false
        override val isAppearanceLightSystemBars: Boolean = true
    }
}