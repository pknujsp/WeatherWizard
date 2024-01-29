package io.github.pknujsp.everyweather.feature.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.route.ParentRoutes
import io.github.pknujsp.everyweather.core.ui.route.Routes

sealed interface SettingsRoutes : Routes {

    companion object : ParentRoutes {
        override val route: String = "Settings"
        @DrawableRes override val navIcon: Int = R.drawable.ic_setting
        @StringRes override val navTitle: Int = io.github.pknujsp.everyweather.core.resource.R.string.nav_settings
    }

    data object Main : SettingsRoutes {
        override val route: String = "Settings/Main"
    }

    data object ValueUnit : SettingsRoutes {
        override val route: String = "Settings/ValueUnit"
    }
}