package io.github.pknujsp.weatherwizard.feature.weather


import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import io.github.pknujsp.weatherwizard.core.common.LocationPermissionManager
import io.github.pknujsp.weatherwizard.core.common.OpenSettingsForLocationPermission
import io.github.pknujsp.weatherwizard.core.common.UnavailableFeature
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherDataArgs
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.ui.RoundedButton
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoScreen

@Composable
@Preview
fun WeatherMainScreen() {
    val mainViewModel: WeatherMainViewModel = hiltViewModel()
    val navController = rememberNavController()

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
            Log.d("WeatherMainScreen", "onActivityResult, refreshKey: $refreshKey")
            openPermissionActivity = false
            refreshKey++
        }
    }

    if (permissionGranted) {
        WeatherInfoScreen(RequestWeatherDataArgs(latitude = 35.236323256911774,
            longitude = 128.86341167027018,
            weatherDataProvider = WeatherDataProvider.Kma))
    } else {
        LocationPermissionManager(onPermissionGranted = {
            permissionGranted = true
            openPermissionActivity = false
            unavailable = false
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
    NavHost(
        navController = navController, route = WeatherRoutes.route,
        startDestination = WeatherRoutes.Main.route) {
        composable(WeatherRoutes.Main.route) {
            WeatherMainScreen()
        }
    }
}

@Composable
fun UnavailableFeatureScreen(@StringRes title: Int, unavailableFeature: UnavailableFeature, onClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 24.dp)
        .statusBarsPadding(),
        verticalArrangement = Arrangement.Center) {
        Text(text = stringResource(title), style = TextStyle(fontSize = 24.sp, color = Color.Black))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(unavailableFeature.message),
            style = TextStyle(fontSize = 16.sp, color = Color.DarkGray)
        )
        Spacer(modifier = Modifier.height(16.dp))
        RoundedButton(
            text = stringResource(id = unavailableFeature.action),
            onClick = onClick,
        )
    }
}


fun NavGraphBuilder.mainWeatherGraph(navController: NavController) {
    navigation(startDestination = WeatherRoutes.Main.route, route = WeatherRoutes.route) {
        composable(WeatherRoutes.Main.route) {
            WeatherMainScreen()
        }
    }
}