package io.github.pknujsp.weatherwizard.feature.componentservice.widget.configure

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.github.pknujsp.weatherwizard.core.ui.ParentRoutes
import io.github.pknujsp.weatherwizard.core.ui.Routes

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