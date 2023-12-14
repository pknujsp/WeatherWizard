package io.github.pknujsp.weatherwizard.feature.weather


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
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.PermissionManager
import io.github.pknujsp.weatherwizard.core.common.manager.PermissionType
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.ui.feature.UnavailableFeatureScreen
import io.github.pknujsp.weatherwizard.core.ui.feature.OpenAppSettingsActivity
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoScreen

@Composable
fun PermissionCheckingScreen(navController: NavController) {
    val mainViewModel: WeatherMainViewModel = hiltViewModel()
    val locationType by mainViewModel.locationType.collectAsStateWithLifecycle()
    var permissionGranted by remember { mutableStateOf(false) }

    var storagePermissionGranted by remember { mutableStateOf(false) }
    var openStoragePermissionActivity by remember { mutableStateOf(false) }

    var unavailable by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableIntStateOf(0) }

    locationType?.apply {
        if (permissionGranted) {
            navigateToInfo(navController)
        } else {

            if (storagePermissionGranted) {
                permissionGranted = locationType is LocationType.CustomLocation

                if (!permissionGranted) {
                    var locationPermissionGranted by remember { mutableStateOf(false) }
                    var openLocationPermissionActivity by remember { mutableStateOf(false) }

                    if (locationPermissionGranted) {
                        permissionGranted = true
                    } else {
                        PermissionManager(PermissionType.LOCATION, onPermissionGranted = {
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
                            UnavailableFeatureScreen(featureType = FeatureType.LOCATION_PERMISSION) {
                                openLocationPermissionActivity = true
                            }
                        }
                        if (openLocationPermissionActivity) {
                            OpenAppSettingsActivity(FeatureType.LOCATION_PERMISSION) {
                                openLocationPermissionActivity = false
                                refreshKey++
                            }
                        }

                    }
                }
            } else {
                PermissionManager(PermissionType.STORAGE, onPermissionGranted = {
                    openStoragePermissionActivity = false
                    unavailable = false
                    storagePermissionGranted = true
                }, onPermissionDenied = {
                    unavailable = true
                }, onShouldShowRationale = {
                    unavailable = true
                }, onNeverAskAgain = {
                    unavailable = true
                }, refreshKey)

                if (unavailable) {
                    UnavailableFeatureScreen(featureType = FeatureType.STORAGE_PERMISSION) {
                        openStoragePermissionActivity = true
                    }
                }
                if (openStoragePermissionActivity) {
                    OpenAppSettingsActivity(FeatureType.STORAGE_PERMISSION) {
                        openStoragePermissionActivity = false
                        refreshKey++
                    }
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