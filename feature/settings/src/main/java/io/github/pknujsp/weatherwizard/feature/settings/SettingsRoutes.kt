package io.github.pknujsp.weatherwizard.feature.settings

import io.github.pknujsp.weatherwizard.core.ui.Routes

sealed interface SettingsRoutes : Routes {

    companion object : Routes {
        override val route: String = "Settings"
    }

    data object Main : SettingsRoutes {
        override val route: String = "Settings/Main"
    }
}