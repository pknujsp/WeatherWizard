package io.github.pknujsp.everyweather.feature.weather.main

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType
import io.github.pknujsp.everyweather.feature.permoptimize.feature.FeatureStateScreen
import io.github.pknujsp.everyweather.feature.permoptimize.feature.rememberFeatureStateManager
import io.github.pknujsp.everyweather.feature.permoptimize.network.rememberNetworkStateManager
import io.github.pknujsp.everyweather.feature.permoptimize.permission.PermissionStateScreen
import io.github.pknujsp.everyweather.feature.permoptimize.permission.rememberPermissionStateManager
import io.github.pknujsp.everyweather.feature.weather.info.WeatherContentScreen

@Composable
fun WeatherMainScreen(
    openDrawer: () -> Unit,
    viewModel: WeatherMainViewModel = hiltViewModel(),
) {
    val currentOpenDrawer by rememberUpdatedState(newValue = openDrawer)
    val selectedLocation by viewModel.selectedLocation.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val networkState = rememberNetworkStateManager()
    val locationPermissionManager = rememberPermissionStateManager(permissionType = FeatureType.Permission.Location)
    val locationServiceState = rememberFeatureStateManager(featureType = FeatureType.LocationService)

    if (selectedLocation != null) {
        val isPassed by remember(
            networkState.isChanged,
            locationServiceState.isChanged,
            locationServiceState.isChanged,
        ) {
            derivedStateOf {
                networkState.isEnabled(context) && (
                    selectedLocation!!.locationType is LocationType.CustomLocation || (
                        locationServiceState.isEnabled(
                            context,
                        ) && locationPermissionManager.isEnabled(context)
                    )
                )
            }
        }

        if (isPassed) {
            WeatherContentScreen(openDrawer = currentOpenDrawer, selectedLocationModel = selectedLocation!!)
        } else if (networkState.isEnabled(context).not()) {
            FeatureStateScreen(featureStateManager = networkState)
        } else if (locationServiceState.isEnabled(context).not()) {
            FeatureStateScreen(featureStateManager = locationServiceState)
        } else {
            PermissionStateScreen(locationPermissionManager)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier,
    openDrawer: () -> Unit,
) {
    Column(modifier = modifier) {
        TopAppBar(
            title = {},
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                ),
            navigationIcon = {
                IconButton(onClick = openDrawer) {
                    Icon(Icons.Rounded.Menu, contentDescription = null)
                }
            },
        )
    }
}