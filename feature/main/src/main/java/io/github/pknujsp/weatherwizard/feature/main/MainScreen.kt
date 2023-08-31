package io.github.pknujsp.weatherwizard.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.weatherwizard.feature.favorite.FavoriteScreen
import io.github.pknujsp.weatherwizard.feature.main.navigation.MainRoutes
import io.github.pknujsp.weatherwizard.feature.settings.SettingsScreen


@Preview
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = MainRoutes.Home.route) {
        composable(MainRoutes.Home.route) {
            HomeScreen()
        }
        composable(MainRoutes.Favorite.route) {
            FavoriteScreen()
        }
        composable(MainRoutes.Settings.route) {
            SettingsScreen()
        }
    }
}