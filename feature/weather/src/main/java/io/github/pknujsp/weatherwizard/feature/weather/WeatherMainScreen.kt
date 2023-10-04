package io.github.pknujsp.weatherwizard.feature.weather


import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.weatherwizard.core.common.LocationPermissionManager
import io.github.pknujsp.weatherwizard.core.common.OpenSettingsForLocationPermission
import io.github.pknujsp.weatherwizard.core.common.UnavailableFeature
import io.github.pknujsp.weatherwizard.core.model.favorite.TargetAreaType
import io.github.pknujsp.weatherwizard.core.ui.UnavailableFeatureScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoScreen

@Composable
fun WeatherMainScreen(navController: NavController, targetAreaType: TargetAreaType) {
    if (targetAreaType is TargetAreaType.CurrentLocation) {
        var permissionGranted by remember { mutableStateOf(false) }
        var openPermissionActivity by remember { mutableStateOf(false) }
        var unavailable by remember { mutableStateOf(false) }
        var refreshKey by remember { mutableIntStateOf(0) }

        if (unavailable) {
            UnavailableFeatureScreen(title = io.github.pknujsp.weatherwizard.core.common.R.string.title_why_you_need_permissions,
                unavailableFeature = UnavailableFeature.LOCATION_PERMISSION_DENIED) {
                openPermissionActivity = true
            }
        }

        if (openPermissionActivity) {
            OpenSettingsForLocationPermission {
                openPermissionActivity = false
                refreshKey++
            }
        }

        if (permissionGranted) {
            navigateToInfo(navController)
            permissionGranted = false
        } else {
            LocationPermissionManager(onPermissionGranted = {
                openPermissionActivity = false
                unavailable = false
                permissionGranted = true
            }, onPermissionDenied = {
                unavailable = true
            }, onShouldShowRationale = {
                unavailable = true
            }, onNeverAskAgain = {
                unavailable = true
            }, refreshKey)
        }
    } else {
        navigateToInfo(navController)
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
    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val mainViewModel: WeatherMainViewModel = hiltViewModel()
    val targetAreaType by mainViewModel.targetAreaType.collectAsStateWithLifecycle()

    targetAreaType?.let { type ->
        NavHost(navController = navController, route = WeatherRoutes.route, startDestination = WeatherRoutes.Main.route) {
            composable(WeatherRoutes.Main.route) {
                WeatherMainScreen(navController, type)
            }
            composable(WeatherRoutes.Info.route) {
                WeatherInfoScreen(navController, navigationBarHeight, type)
            }
        }
    }
}