package io.github.pknujsp.everyweather.feature.weather

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.everyweather.feature.weather.main.WeatherMainScreen
import io.github.pknujsp.everyweather.feature.weather.route.WeatherRoutes

@Composable
fun HostWeatherScreen(openDrawer: () -> Unit) {
    val navController = rememberNavController()
    val currentOpenDrawer by rememberUpdatedState(openDrawer)

    NavHost(navController = navController, route = WeatherRoutes.route, startDestination = WeatherRoutes.Info.route) {
        composable(WeatherRoutes.Info.route) {
            WeatherMainScreen(currentOpenDrawer)
        }
    }
}