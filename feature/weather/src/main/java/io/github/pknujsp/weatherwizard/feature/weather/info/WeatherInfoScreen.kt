package io.github.pknujsp.weatherwizard.feature.weather.info


import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.common.GpsLocationManager
import io.github.pknujsp.weatherwizard.core.common.UnavailableFeature
import io.github.pknujsp.weatherwizard.core.common.util.AStyle
import io.github.pknujsp.weatherwizard.core.common.util.toAnnotated
import io.github.pknujsp.weatherwizard.core.model.favorite.TargetAreaType
import io.github.pknujsp.weatherwizard.core.model.onRunning
import io.github.pknujsp.weatherwizard.core.model.onSucceed
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithoutNavigation
import io.github.pknujsp.weatherwizard.core.ui.UnavailableFeatureScreen
import io.github.pknujsp.weatherwizard.core.ui.dialog.BottomSheet
import io.github.pknujsp.weatherwizard.core.ui.lottie.NonCancellableLoadingScreen
import io.github.pknujsp.weatherwizard.core.ui.theme.notIncludeTextPaddingStyle
import io.github.pknujsp.weatherwizard.core.ui.theme.outlineTextStyle
import io.github.pknujsp.weatherwizard.feature.airquality.AirQualityScreen
import io.github.pknujsp.weatherwizard.feature.flickr.FlickrImageItemScreen
import io.github.pknujsp.weatherwizard.feature.map.SimpleMapScreen
import io.github.pknujsp.weatherwizard.feature.sunsetrise.SimpleSunSetRiseScreen
import io.github.pknujsp.weatherwizard.feature.weather.CustomTopAppBar
import io.github.pknujsp.weatherwizard.feature.weather.CustomTopAppBarColors
import io.github.pknujsp.weatherwizard.feature.weather.NestedWeatherRoutes
import io.github.pknujsp.weatherwizard.feature.weather.R
import io.github.pknujsp.weatherwizard.feature.weather.comparison.dailyforecast.CompareDailyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.comparison.hourlyforecast.CompareHourlyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.currentweather.simple.CurrentWeatherScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.dailyforecast.detail.DetailDailyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.dailyforecast.simple.SimpleDailyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.detail.DetailHourlyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.simple.HourlyForecastScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherInfoScreen(navController: NavController, targetAreaType: TargetAreaType) {
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current)
    val weatherInfoViewModel: WeatherInfoViewModel = hiltViewModel(viewModelStoreOwner)
    var nestedRoutes by rememberSaveable(saver = Saver(save = { it.value.route },
        restore = { mutableStateOf(NestedWeatherRoutes.getRoute(it)) })) {
        mutableStateOf(NestedWeatherRoutes.startDestination)
    }

    val scrollState = rememberScrollState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val args by weatherInfoViewModel.args.collectAsStateWithLifecycle()
    val processState by weatherInfoViewModel.processState.collectAsStateWithLifecycle()
    val headInfo by weatherInfoViewModel.reverseGeoCode.collectAsStateWithLifecycle()
    var backgroundImageUrl by remember { mutableStateOf("") }
    var reload by remember { mutableIntStateOf(0) }

    val gpsLocationManager = GpsLocationManager(LocalContext.current)
    var enabledLocation by remember { mutableStateOf(gpsLocationManager.isGpsProviderEnabled()) }
    var openLocationSettings by remember { mutableStateOf(false) }
    var onClickedWeatherProviderButton by remember { mutableStateOf(false) }
    var showLocationLoadingDialog by remember { mutableStateOf(false) }

    val window = (LocalContext.current as Activity).window
    val windowInsetsController = remember { WindowCompat.getInsetsController(window, window.decorView) }

    DisposableEffect(Unit) {
        weatherInfoViewModel.setLastTargetAreaType(targetAreaType)
        onDispose { }
    }

    LaunchedEffect(targetAreaType, reload) {
        load(targetAreaType, gpsLocationManager, weatherInfoViewModel, enabledLocation = {
            enabledLocation = it
        }, showLocationLoadingDialog = {
            showLocationLoadingDialog = it
        })
    }

    when (nestedRoutes) {
        is NestedWeatherRoutes.Main -> {
            windowInsetsController.isAppearanceLightNavigationBars = false
            WeatherContentScreen()
        }

        is NestedWeatherRoutes.DetailHourlyForecast -> {
            windowInsetsController.isAppearanceLightNavigationBars = true
            DetailHourlyForecastScreen(weatherInfoViewModel) {
                nestedRoutes = NestedWeatherRoutes.Main
            }
        }

        is NestedWeatherRoutes.DetailDailyForecast -> {
            windowInsetsController.isAppearanceLightNavigationBars = true
            DetailDailyForecastScreen(weatherInfoViewModel) {
                nestedRoutes = NestedWeatherRoutes.Main
            }
        }

        is NestedWeatherRoutes.ComparisonDailyForecast -> {
            args?.let {
                windowInsetsController.isAppearanceLightNavigationBars = true
                CompareDailyForecastScreen(it) {
                    nestedRoutes = NestedWeatherRoutes.Main
                }
            }
        }

        is NestedWeatherRoutes.ComparisonHourlyForecast -> {
            args?.let {
                windowInsetsController.isAppearanceLightNavigationBars = true
                CompareHourlyForecastScreen(it, viewModelStoreOwner) {
                    nestedRoutes = NestedWeatherRoutes.Main
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeatherProviderDialog(currentProvider: WeatherDataProvider, onClick: (WeatherDataProvider?) -> Unit) {
    BottomSheet(
        onDismissRequest = {
            onClick(null)
        },
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            TitleTextWithoutNavigation(title = stringResource(id = R.string.weather_provider))
            WeatherDataProvider.providers.forEach { weatherDataProvider ->
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
    targetAreaType: TargetAreaType,
    gpsLocationManager: GpsLocationManager,
    weatherInfoViewModel: WeatherInfoViewModel,
    enabledLocation: (Boolean) -> Unit,
    showLocationLoadingDialog: (Boolean) -> Unit
) {
    weatherInfoViewModel.waitForLoad()
    if (targetAreaType is TargetAreaType.CurrentLocation) {
        if (gpsLocationManager.isGpsProviderEnabled()) {
            enabledLocation(true)
            showLocationLoadingDialog(true)
            val result = gpsLocationManager.getCurrentLocation()
            if (result is GpsLocationManager.CurrentLocationResult.Success) {
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

@Stable
private fun shardowBox(
): Brush = Brush.linearGradient(
    0.0f to Color.Black.copy(alpha = 0.5f),
    1.0f to Color.Transparent,
    start = Offset(0.0f, 0f),
    end = Offset(0.0f, Float.POSITIVE_INFINITY),
    tileMode = TileMode.Clamp
)