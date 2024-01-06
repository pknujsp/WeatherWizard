package io.github.pknujsp.weatherwizard.feature.weather


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.PermissionType
import io.github.pknujsp.weatherwizard.core.common.manager.checkSelfPermission
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.ui.feature.OpenAppSettingsActivity
import io.github.pknujsp.weatherwizard.core.ui.feature.UnavailableFeatureScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoScreen
import io.github.pknujsp.weatherwizard.feature.weather.route.WeatherRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionCheckingScreen(navController: NavController, openDrawer: () -> Unit, mainViewModel: WeatherMainViewModel = hiltViewModel()) {
    val locationType = mainViewModel.locationType
    locationType?.run {
        val context = LocalContext.current
        var openLocationPermissionActivity by remember { mutableStateOf(false) }
        var locationPermissionGranted by remember { mutableStateOf(context.checkSelfPermission(PermissionType.LOCATION)) }

        if (locationPermissionGranted || locationType is LocationType.CustomLocation) {
            navigateToInfo(navController)
        } else {
            Column(modifier = Modifier.systemBarsPadding()) {
                TopAppBar(title = {},
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                    ),
                    navigationIcon = {
                        IconButton(onClick = openDrawer) {
                            Icon(Icons.Rounded.Menu, contentDescription = null)
                        }
                    })
                UnavailableFeatureScreen(featureType = FeatureType.LOCATION_PERMISSION) {
                    openLocationPermissionActivity = true
                    locationPermissionGranted = context.checkSelfPermission(PermissionType.LOCATION)
                }
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
fun HostWeatherScreen(openDrawer: () -> Unit) {
    val navController = rememberNavController()

    NavHost(navController = navController, route = WeatherRoutes.route, startDestination = WeatherRoutes.Main.route) {
        composable(WeatherRoutes.Main.route) {
            PermissionCheckingScreen(navController, openDrawer)
        }
        composable(WeatherRoutes.Info.route) {
            WeatherInfoScreen(openDrawer)
        }
    }

}