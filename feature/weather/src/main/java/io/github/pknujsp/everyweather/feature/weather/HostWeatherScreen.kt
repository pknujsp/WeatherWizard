package io.github.pknujsp.everyweather.feature.weather

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.everyweather.feature.weather.info.WeatherInfoScreen
import io.github.pknujsp.everyweather.feature.weather.route.WeatherRoutes

@Composable
fun HostWeatherScreen(openDrawer: () -> Unit) {
    val navController = rememberNavController()

    NavHost(navController = navController, route = WeatherRoutes.route, startDestination = WeatherRoutes.Main.route) {
        composable(WeatherRoutes.Main.route) {
            PrimaryFeatureChecker(navController, openDrawer)
        }
        composable(WeatherRoutes.Info.route) {
            WeatherInfoScreen(openDrawer)
        }
    }

}