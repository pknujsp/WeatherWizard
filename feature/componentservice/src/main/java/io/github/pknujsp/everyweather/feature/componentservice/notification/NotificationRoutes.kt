package io.github.pknujsp.everyweather.feature.componentservice.notification

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.route.ParentRoutes
import io.github.pknujsp.everyweather.core.ui.route.Routes
import io.github.pknujsp.everyweather.core.ui.route.RoutesWithArgument

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
            get() =
                listOf(
                    navArgument("notificationId") { type = NavType.LongType },
                )
    }
}
