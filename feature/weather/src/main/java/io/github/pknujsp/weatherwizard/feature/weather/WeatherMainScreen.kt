package io.github.pknujsp.weatherwizard.feature.weather


import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.weatherwizard.core.common.LocationPermissionManager
import io.github.pknujsp.weatherwizard.core.common.OpenSettingsForLocationPermission
import io.github.pknujsp.weatherwizard.core.common.UnavailableFeature
import io.github.pknujsp.weatherwizard.core.ui.UnavailableFeatureScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoScreen

@Composable
fun WeatherMainScreen(navController: NavController) {
    val mainViewModel: WeatherMainViewModel = hiltViewModel()

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
        navController.navigate(WeatherRoutes.Info.route) {
            launchSingleTop = true
            popUpTo(WeatherRoutes.Main.route) { inclusive = true }
        }
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
}

@Composable
fun HostWeatherScreen() {
    val navController = rememberNavController()
    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    NavHost(navController = navController, route = WeatherRoutes.route, startDestination = WeatherRoutes.Main.route) {
        composable(WeatherRoutes.Main.route) {
            WeatherMainScreen(navController)
        }
        composable(WeatherRoutes.Info.route) {
            WeatherInfoScreen(navController, navigationBarHeight)
        }
    }
}