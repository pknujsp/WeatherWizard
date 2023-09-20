package io.github.pknujsp.weatherwizard.feature.settings

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

@Composable
fun SettingsScreen() {

}

fun NavGraphBuilder.settingsGraph() {
    navigation(startDestination = SettingsRoutes.Main.route, route = SettingsRoutes.route) {
        composable(SettingsRoutes.Main.route) { SettingsScreen() }
    }
}