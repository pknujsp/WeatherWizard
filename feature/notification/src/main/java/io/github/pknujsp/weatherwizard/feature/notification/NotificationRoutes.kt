package io.github.pknujsp.weatherwizard.feature.notification

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.github.pknujsp.weatherwizard.core.common.R
import io.github.pknujsp.weatherwizard.core.ui.ParentRoutes
import io.github.pknujsp.weatherwizard.core.ui.Routes

sealed interface NotificationRoutes : Routes {

    companion object : ParentRoutes {
        override val route: String = "Notification"
        @DrawableRes override val navIcon: Int = R.drawable.round_notifications_24
        @StringRes override val navTitle: Int = R.string.nav_notification
    }

    data object Main : NotificationRoutes {
        override val route: String = "Notification/Main"
    }

    data object Permission : NotificationRoutes {
        override val route: String = "Notification/Permission"
    }


    data object Ongoing : NotificationRoutes {
        override val route: String = "Notification/Ongoing"
    }

    data object Daily : NotificationRoutes {
        override val route: String = "Notification/Daily"
    }
}