package io.github.pknujsp.weatherwizard.feature.widget.activity.configure

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import io.github.pknujsp.weatherwizard.core.common.R
import io.github.pknujsp.weatherwizard.core.ui.ParentRoutes
import io.github.pknujsp.weatherwizard.core.ui.Routes
import io.github.pknujsp.weatherwizard.core.ui.RoutesWithArgument

sealed interface WidgetRoutes : Routes {

    companion object : ParentRoutes {
        override val route: String = "WidgetConfigure"
        @DrawableRes override val navIcon: Int = 0
        @StringRes override val navTitle: Int = 0
    }

    data object Configure : RoutesWithArgument("WidgetConfigure/Configure") {
        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument("widgetId") { type = NavType.IntType },
                navArgument("widgetType") { type = NavType.IntType },
            )
    }
}