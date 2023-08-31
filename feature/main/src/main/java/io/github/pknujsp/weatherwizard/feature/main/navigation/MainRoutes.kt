package io.github.pknujsp.weatherwizard.feature.main.navigation

sealed class MainRoutes(override val route: String) : Routes(route) {
    object Favorite : MainRoutes("favorite")
    object Home : MainRoutes("home")

    object Settings : MainRoutes("settings")
}