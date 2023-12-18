package io.github.pknujsp.weatherwizard.feature.widget.activity.configure

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.ParentRoutes
import io.github.pknujsp.weatherwizard.core.ui.Routes
import io.github.pknujsp.weatherwizard.core.ui.RoutesWithArgument

sealed interface WidgetRoutes : Routes {

    companion object : ParentRoutes {
        override val route: String = "WidgetConfigure"
        @DrawableRes override val navIcon: Int = 0
        @StringRes override val navTitle: Int = 0
    }

    data object Configure : WidgetRoutes {
        override val route: String = "WidgetConfigure/Configure"
    }
}