package io.github.pknujsp.weatherwizard.feature.weather


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.PermissionManager
import io.github.pknujsp.weatherwizard.core.common.manager.PermissionType
import io.github.pknujsp.weatherwizard.core.common.manager.checkSelfPermission
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.ui.feature.OpenAppSettingsActivity
import io.github.pknujsp.weatherwizard.core.ui.feature.UnavailableFeatureScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoScreen

@Composable
fun PermissionCheckingScreen(navController: NavController, mainViewModel: WeatherMainViewModel = hiltViewModel()) {
    val locationType by mainViewModel.locationType.collectAsStateWithLifecycle()
    locationType?.run {
        val context = LocalContext.current
        var openLocationPermissionActivity by remember { mutableStateOf(false) }
        var locationPermissionGranted by remember { mutableStateOf(context.checkSelfPermission(PermissionType.LOCATION)) }

        if (locationPermissionGranted || locationType is LocationType.CustomLocation) {
            navigateToInfo(navController)
        } else {
            UnavailableFeatureScreen(featureType = FeatureType.LOCATION_PERMISSION) {
                openLocationPermissionActivity = true
                locationPermissionGranted = context.checkSelfPermission(PermissionType.LOCATION)
            }
            if (openLocationPermissionActivity) {
                OpenAppSettingsActivity(FeatureType.LOCATION_PERMISSION) {
                    openLocationPermissionActivity = false
                }
            }
        }
    }

}

private fun navigateToInfo(navController: NavController) {
    navController.navigate(WeatherRoutes.Info.route) {
        launchSingleTop = true
        popUpTo(WeatherRoutes.Main.route) { inclusive = true }
    }
}

@Composable
fun HostWeatherScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, route = WeatherRoutes.route, startDestination = WeatherRoutes.Main.route) {
        composable(WeatherRoutes.Main.route) {
            PermissionCheckingScreen(navController)
        }
        composable(WeatherRoutes.Info.route) {
            WeatherInfoScreen(navController)
        }
    }

}