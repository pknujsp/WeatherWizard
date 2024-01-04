package io.github.pknujsp.weatherwizard.feature.componentservice.notification

import android.app.Activity
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.daily.ConfigDailyNotificationScreen
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.daily.DailyNotificationListScreen
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.ongoing.OngoingNotificationScreen


@Composable
fun HostNotificationScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, route = NotificationRoutes.route, startDestination = NotificationRoutes.Main.route,
        modifier = Modifier.systemBarsPadding()) {
        composable(NotificationRoutes.Main.route) {
            NotificationMainScreen(navController = navController)
        }
        composable(NotificationRoutes.Ongoing.route) {
            OngoingNotificationScreen(navController = navController)
        }
        composable(NotificationRoutes.Daily.route) {
            DailyNotificationListScreen(navController = navController)
        }
        composable(NotificationRoutes.AddOrEditDaily.route, arguments = NotificationRoutes.AddOrEditDaily.arguments) {
            ConfigDailyNotificationScreen(navController)
        }

    }
}