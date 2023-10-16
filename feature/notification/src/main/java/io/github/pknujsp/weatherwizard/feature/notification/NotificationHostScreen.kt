package io.github.pknujsp.weatherwizard.feature.notification

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.weatherwizard.feature.notification.daily.AddOrEditDailyNotificationScreen
import io.github.pknujsp.weatherwizard.feature.notification.daily.DailyNotificationListScreen
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.OngoingNotificationScreen


@Composable
fun HostNotificationScreen() {
    val navController = rememberNavController()
    val window = (LocalContext.current as Activity).window
    WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = true

    NavHost(navController = navController, route = NotificationRoutes.route, startDestination = if (Build.VERSION.SDK_INT >= Build
            .VERSION_CODES.TIRAMISU) NotificationRoutes.Permission.route else NotificationRoutes.Main.route,
        modifier = Modifier.navigationBarsPadding()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            composable(NotificationRoutes.Permission.route) {
                PermissionCheckingScreen(navController = navController)
            }
        }
        composable(NotificationRoutes.Main.route) {
            NotificationMainScreen(navController = navController)
        }
        composable(NotificationRoutes.Ongoing.route) {
            OngoingNotificationScreen(navController = navController)
        }
        composable(NotificationRoutes.Daily.route) {
            DailyNotificationListScreen(navController = navController)
        }
        composable(NotificationRoutes.AddOrEditDaily.route, arguments = NotificationRoutes.AddOrEditDaily.arguments.values.toList()) {
            AddOrEditDailyNotificationScreen(navController, it.arguments!!.getLong("id"))
        }
    }
}