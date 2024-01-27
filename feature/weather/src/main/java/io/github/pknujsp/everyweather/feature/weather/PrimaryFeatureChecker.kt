package io.github.pknujsp.everyweather.feature.weather


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
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.core.common.manager.PermissionType
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType
import io.github.pknujsp.everyweather.core.ui.feature.PermissionStateScreen
import io.github.pknujsp.everyweather.feature.weather.route.WeatherRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PrimaryFeatureChecker(
    navController: NavController,
    openDrawer: () -> Unit,
    mainViewModel: WeatherMainViewModel = hiltViewModel()
) {
    val locationType = mainViewModel.locationType
    locationType?.run {
        val context = LocalContext.current
        var locationPermissionGranted by remember {
            mutableStateOf(FeatureType.LOCATION_PERMISSION.isAvailable(context))
        }

        if (locationPermissionGranted || locationType is LocationType.CustomLocation) {
            navController.navigate(WeatherRoutes.Info.route) {
                launchSingleTop = true
                popUpTo(WeatherRoutes.Main.route) { inclusive = true }
            }
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
                PermissionStateScreen(permissionType = PermissionType.LOCATION) {
                    locationPermissionGranted = true
                }
            }
        }
    }

}