package io.github.pknujsp.everyweather.feature.componentservice.notification

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.everyweather.feature.componentservice.notification.daily.ConfigDailyNotificationScreen
import io.github.pknujsp.everyweather.feature.componentservice.notification.daily.DailyNotificationListScreen
import io.github.pknujsp.everyweather.feature.componentservice.notification.ongoing.OngoingNotificationScreen

@Composable
fun HostNotificationScreen() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        route = NotificationRoutes.route,
        startDestination = NotificationRoutes.Main.route,
        modifier = Modifier.systemBarsPadding(),
    ) {
        composable(NotificationRoutes.Main.route) {
            NotificationMainScreen(navController)
        }
        composable(NotificationRoutes.Ongoing.route) {
            OngoingNotificationScreen(navController)
        }
        composable(NotificationRoutes.Daily.route) {
            DailyNotificationListScreen(navController)
        }
        composable(NotificationRoutes.AddOrEditDaily.route, arguments = NotificationRoutes.AddOrEditDaily.arguments) {
            ConfigDailyNotificationScreen(navController)
        }
    }
}