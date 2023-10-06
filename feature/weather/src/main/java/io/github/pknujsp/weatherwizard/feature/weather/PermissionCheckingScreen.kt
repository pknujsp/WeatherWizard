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
fun PermissionCheckingScreen(navController: NavController, targetAreaType: TargetAreaType) {
    var permissionGranted by remember { mutableStateOf(targetAreaType is TargetAreaType.CustomLocation) }

    var unavailable by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableIntStateOf(0) }

    var locationPermissionGranted by remember { mutableStateOf(false) }
    var openLocationPermissionActivity by remember { mutableStateOf(false) }

    //var storagePermissionGranted by remember { mutableStateOf(false) }
    //var openStoragePermissionActivity by remember { mutableStateOf(false) }

    if (permissionGranted) {
        navigateToInfo(navController)
    } else {

        if (locationPermissionGranted) {
            permissionGranted = true
        } else {
            LocationPermissionManager(onPermissionGranted = {
                openLocationPermissionActivity = false
                unavailable = false
                locationPermissionGranted = true
            }, onPermissionDenied = {
                unavailable = true
            }, onShouldShowRationale = {
                unavailable = true
            }, onNeverAskAgain = {
                unavailable = true
            }, refreshKey)

            if (unavailable) {
                UnavailableFeatureScreen(title = io.github.pknujsp.weatherwizard.core.common.R.string.title_why_you_need_permissions,
                    unavailableFeature = UnavailableFeature.LOCATION_PERMISSION_DENIED) {
                    openLocationPermissionActivity = true
                }
            }
            if (openLocationPermissionActivity) {
                OpenSettingsForLocationPermission {
                    openLocationPermissionActivity = false
                    refreshKey++
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
    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val mainViewModel: WeatherMainViewModel = hiltViewModel()
    val targetAreaType by mainViewModel.targetAreaType.collectAsStateWithLifecycle()

    targetAreaType?.let { type ->
        NavHost(navController = navController, route = WeatherRoutes.route, startDestination = WeatherRoutes.Main.route) {
            composable(WeatherRoutes.Main.route) {
                PermissionCheckingScreen(navController, type)
            }
            composable(WeatherRoutes.Info.route) {
                WeatherInfoScreen(navController, navigationBarHeight, type)
            }
        }
    }
}