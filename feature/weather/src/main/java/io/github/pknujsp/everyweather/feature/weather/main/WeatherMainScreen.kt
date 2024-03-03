package io.github.pknujsp.everyweather.feature.weather.main


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.core.data.favorite.SelectedLocationModel
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType
import io.github.pknujsp.everyweather.feature.permoptimize.feature.FeatureStateScreen
import io.github.pknujsp.everyweather.feature.permoptimize.feature.rememberAppFeatureState
import io.github.pknujsp.everyweather.feature.permoptimize.network.rememberAppNetworkState
import io.github.pknujsp.everyweather.feature.permoptimize.permission.PermissionState
import io.github.pknujsp.everyweather.feature.permoptimize.permission.PermissionStateScreen
import io.github.pknujsp.everyweather.feature.permoptimize.permission.rememberPermissionManager
import io.github.pknujsp.everyweather.feature.weather.info.WeatherContentScreen

@Composable
fun WeatherMainScreen(
    openDrawer: () -> Unit,
    viewModel: WeatherMainViewModel = hiltViewModel(),
) {
    val currentOpenDrawer by rememberUpdatedState(newValue = openDrawer)
    val selectedLocation by viewModel.selectedLocation.collectAsStateWithLifecycle()
    var isPassed by remember { mutableStateOf(false) }

    val networkState = rememberAppNetworkState()
    val locationPermissionManager = rememberPermissionManager(defaultPermissionType = FeatureType.Permission.Location)
    val locationServiceState = rememberAppFeatureState(featureType = FeatureType.LocationService)

    if (selectedLocation != null) {
        LaunchedEffect(selectedLocation,
            networkState.isAvailable,
            locationServiceState.isAvailable,
            locationPermissionManager.permissionState) {
            // 네트워크 우선 체크
            isPassed = if (networkState.isAvailable) {
                when (selectedLocation!!.locationType) {
                    // 위치 서비스 -> 위치 권한
                    is LocationType.CurrentLocation -> locationServiceState.isAvailable && locationPermissionManager.permissionState is PermissionState.Granted
                    is LocationType.CustomLocation -> true
                }
            } else {
                false
            }
        }

        if (isPassed) {
            WeatherContentScreen(openDrawer = currentOpenDrawer, selectedLocationModel = selectedLocation!!)
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                TopBar(openDrawer = currentOpenDrawer, modifier = Modifier
                    .systemBarsPadding()
                    .fillMaxWidth())
                if (networkState.isAvailable.not()) {
                    FeatureStateScreen(featureStateManager = networkState)
                } else if (locationServiceState.isAvailable.not()) {
                    FeatureStateScreen(featureStateManager = locationServiceState)
                } else {
                    PermissionStateScreen(locationPermissionManager)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(modifier: Modifier, openDrawer: () -> Unit) {
    Column(modifier = modifier) {
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
    }
}