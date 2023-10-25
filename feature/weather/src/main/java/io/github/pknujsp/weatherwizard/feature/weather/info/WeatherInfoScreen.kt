package io.github.pknujsp.weatherwizard.feature.weather.info


import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.common.manager.AppLocationManager
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithoutNavigation
import io.github.pknujsp.weatherwizard.core.ui.dialog.BottomSheet
import io.github.pknujsp.weatherwizard.feature.weather.NestedWeatherRoutes
import io.github.pknujsp.weatherwizard.feature.weather.R
import io.github.pknujsp.weatherwizard.feature.weather.comparison.dailyforecast.CompareDailyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.comparison.hourlyforecast.CompareHourlyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.dailyforecast.detail.DetailDailyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.detail.DetailHourlyForecastScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherInfoScreen(navController: NavController) {
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current)
    val viewModel: WeatherInfoViewModel = hiltViewModel(viewModelStoreOwner)

    var nestedRoutes by rememberSaveable(saver = Saver(save = { it.value.route },
        restore = { mutableStateOf(NestedWeatherRoutes.getRoute(it)) })) {
        mutableStateOf(NestedWeatherRoutes.startDestination)
    }

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val arguments by viewModel.args.collectAsStateWithLifecycle()
    val processState by viewModel.processState.collectAsStateWithLifecycle()
    var backgroundImageUrl by remember { mutableStateOf("") }
    var reload by remember { mutableIntStateOf(0) }

    val gpsLocationManager = remember { AppLocationManager.getInstance(context) }
    var enabledLocation by remember { mutableStateOf(gpsLocationManager.isGpsProviderEnabled()) }
    var showLocationLoadingDialog by remember { mutableStateOf(false) }

    val window = (LocalContext.current as Activity).window
    val windowInsetsController = remember { WindowCompat.getInsetsController(window, window.decorView) }

    LaunchedEffect(reload) {
        load(viewModel.locationType, gpsLocationManager, viewModel, enabledLocation = {
            enabledLocation = it
        }, showLocationLoadingDialog = {
            showLocationLoadingDialog = it
        })
    }

    when (nestedRoutes) {
        is NestedWeatherRoutes.Main -> {
            windowInsetsController.isAppearanceLightNavigationBars = false
            arguments?.let {
                val contentArguments =
                    ContentArguments(
                        arguments!!,
                        scrollState,
                        scrollBehavior,
                        processState,
                        enabledLocation,
                        backgroundImageUrl,
                        navigate = {
                            nestedRoutes = it
                        },
                        reload = {
                            reload++
                        },
                        onChangedBackgroundImageUrl = {
                            backgroundImageUrl = it
                        }
                    )
                WeatherContentScreen(contentArguments, viewModel)
            }
        }

        is NestedWeatherRoutes.DetailHourlyForecast -> {
            windowInsetsController.isAppearanceLightNavigationBars = true
            DetailHourlyForecastScreen(viewModel) {
                nestedRoutes = NestedWeatherRoutes.Main
            }
        }

        is NestedWeatherRoutes.DetailDailyForecast -> {
            windowInsetsController.isAppearanceLightNavigationBars = true
            DetailDailyForecastScreen(viewModel) {
                nestedRoutes = NestedWeatherRoutes.Main
            }
        }

        is NestedWeatherRoutes.ComparisonDailyForecast -> {
            windowInsetsController.isAppearanceLightNavigationBars = true
            arguments?.let {
                CompareDailyForecastScreen(it) {
                    nestedRoutes = NestedWeatherRoutes.Main
                }
            }
        }

        is NestedWeatherRoutes.ComparisonHourlyForecast -> {
            windowInsetsController.isAppearanceLightNavigationBars = true
            arguments?.let {
                CompareHourlyForecastScreen(it, viewModelStoreOwner) {
                    nestedRoutes = NestedWeatherRoutes.Main
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherProviderDialog(currentProvider: WeatherDataProvider, onClick: (WeatherDataProvider?) -> Unit) {
    BottomSheet(
        onDismissRequest = {
            onClick(null)
        },
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            TitleTextWithoutNavigation(title = stringResource(id = R.string.weather_provider))
            WeatherDataProvider.enums.forEach { weatherDataProvider ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .clickable {
                        onClick(weatherDataProvider)
                    }
                    .fillMaxWidth()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(weatherDataProvider.logo).crossfade(false).build(),
                        contentDescription = stringResource(id = R.string.weather_provider),
                        modifier = Modifier
                            .size(34.dp)
                            .padding(start = 12.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(id = weatherDataProvider.name),
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f))
                    RadioButton(selected = currentProvider == weatherDataProvider, onClick = {
                        onClick(weatherDataProvider)
                    }, modifier = Modifier.padding(end = 12.dp))
                }
            }
        }
    }
}

private suspend fun load(
    locationType: LocationType,
    gpsLocationManager: AppLocationManager,
    weatherInfoViewModel: WeatherInfoViewModel,
    enabledLocation: (Boolean) -> Unit,
    showLocationLoadingDialog: (Boolean) -> Unit
) {
    weatherInfoViewModel.waitForLoad()
    if (locationType is LocationType.CurrentLocation) {
        if (gpsLocationManager.isGpsProviderEnabled()) {
            enabledLocation(true)
            showLocationLoadingDialog(true)
            val result = gpsLocationManager.getCurrentLocation()
            if (result is AppLocationManager.CurrentLocationResult.Success) {
                weatherInfoViewModel.setArgsAndLoad(result.location)
            }
            showLocationLoadingDialog(false)
        } else {
            enabledLocation(false)
        }
    } else {
        weatherInfoViewModel.setArgsAndLoad()
    }
}