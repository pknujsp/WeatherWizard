package io.github.pknujsp.weatherwizard.feature.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.github.pknujsp.weatherwizard.core.common.R
import io.github.pknujsp.weatherwizard.core.ui.ParentRoutes
import io.github.pknujsp.weatherwizard.core.ui.Routes

sealed interface SettingsRoutes : Routes {

    companion object : ParentRoutes {
        override val route: String = "Settings"
        @DrawableRes override val navIcon: Int = R.drawable.baseline_settings_24
        @StringRes override val navTitle: Int = R.string.nav_settings
    }

    data object Main : SettingsRoutes {
        override val route: String = "Main"
    }
}