package io.github.pknujsp.weatherwizard.feature.settings

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun SettingsScreen() {

}


@Composable
fun HostSettingsScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, route = SettingsRoutes.route, startDestination = SettingsRoutes.Main.route) {
        composable(SettingsRoutes.Main.route) { SettingsScreen() }
    }
}