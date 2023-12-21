package io.github.pknujsp.weatherwizard.feature.componentservice.notification

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.ParentRoutes
import io.github.pknujsp.weatherwizard.core.ui.Routes
import io.github.pknujsp.weatherwizard.core.ui.RoutesWithArgument

sealed interface NotificationRoutes : Routes {

    companion object : ParentRoutes {
        override val route: String = "Notification"
        @DrawableRes override val navIcon: Int = R.drawable.round_notifications_24
        @StringRes override val navTitle: Int = R.string.nav_notification
    }

    data object Main : NotificationRoutes {
        override val route: String = "Notification/Main"
    }

    data object Ongoing : NotificationRoutes {
        override val route: String = "Notification/Ongoing"
    }

    data object Daily : NotificationRoutes {
        override val route: String = "Notification/Daily"
    }

    data object AddOrEditDaily : RoutesWithArgument("Notification/AddOrEditDaily") {
        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument("notificationId") { type = NavType.LongType },
            )
    }
}