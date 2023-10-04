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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
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
fun WeatherInfoScreen(navController: NavController, navigationBarHeight: Dp, targetAreaType: TargetAreaType) {
    val weatherInfoViewModel: WeatherInfoViewModel = hiltViewModel()
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

            Box {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    model = ImageRequest.Builder(LocalContext.current).run {
                        crossfade(200)
                        if (backgroundImageUrl.isEmpty()) data(io.github.pknujsp.weatherwizard.core.common.R.drawable.bg_grad)
                        else data(backgroundImageUrl)
                        build()
                    },
                    contentDescription = stringResource(R.string.background_image),
                    filterQuality = FilterQuality.High,
                )

                processState.onRunning {
                    NonCancellableLoadingScreen(stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.loading_weather_data)) {

                    }
                }.onSucceed {
                    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        containerColor = Color.Black.copy(alpha = 0.17f),
                        topBar = {
                            CustomTopAppBar(smallTitle = {
                                Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center) {
                                    headInfo.onSuccess {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Rounded.Place,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .padding(end = 4.dp))
                                            Text(
                                                text = it.displayName,
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                style = LocalTextStyle.current.merge(notIncludeTextPaddingStyle).merge(outlineTextStyle),
                                            )
                                        }
                                        Text(
                                            text = it.requestDateTime,
                                            fontSize = TextUnit(11f, TextUnitType.Sp),
                                            color = Color.White,
                                            style = LocalTextStyle.current.merge(notIncludeTextPaddingStyle).merge(outlineTextStyle),
                                        )
                                    }
                                }
                            },
                                bigTitle = {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(end = 60.dp),
                                    ) {
                                        headInfo.onSuccess { it ->
                                            Text(
                                                text = listOf(
                                                    AStyle(
                                                        "${it.country}\n",
                                                        span = SpanStyle(
                                                            fontSize = TextUnit(18f, TextUnitType.Sp),
                                                        ),
                                                    ),
                                                    AStyle(it.displayName, span = SpanStyle(fontSize = TextUnit(24f, TextUnitType.Sp))),
                                                ).toAnnotated(),
                                                textAlign = TextAlign.Start,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                lineHeight = 28.sp,
                                                style = LocalTextStyle.current.merge(outlineTextStyle),
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(
                                                horizontalArrangement = Arrangement.Start,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(LocalContext.current)
                                                        .data(io.github.pknujsp.weatherwizard.core.common.R.drawable.ic_time)
                                                        .crossfade(false).build(),
                                                    contentDescription = stringResource(id = io.github.pknujsp.weatherwizard.core.model.R.string.weather_info_head_info_update_time),
                                                    colorFilter = ColorFilter.tint(Color.White),
                                                    modifier = Modifier.size(16.dp),
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = it.requestDateTime,
                                                    fontSize = 14.sp,
                                                    color = Color.White, style = LocalTextStyle.current.merge(outlineTextStyle),
                                                )
                                            }
                                            Row(horizontalArrangement = Arrangement.Start,
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.clickable {
                                                    onClickedWeatherProviderButton = true
                                                }) {
                                                args?.let { args ->
                                                    AsyncImage(
                                                        model = ImageRequest.Builder(LocalContext.current)
                                                            .data(args.weatherDataProvider.logo).crossfade(false).build(),
                                                        contentDescription = stringResource(id = R.string.weather_provider),
                                                        modifier = Modifier.size(16.dp),
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = stringResource(id = args.weatherDataProvider.name),
                                                        fontSize = 14.sp,
                                                        color = Color.White,
                                                        style = LocalTextStyle.current.merge(outlineTextStyle),
                                                    )
                                                }
                                            }
                                        }
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { reload++ }) {
                                        Icon(painter = painterResource(id = io.github.pknujsp.weatherwizard.core.common.R.drawable.ic_refresh),
                                            contentDescription = null)
                                    }
                                },
                                scrollBehavior = scrollBehavior,
                                colors = CustomTopAppBarColors(
                                    containerColor = Color.Transparent,
                                    scrolledContainerColor = Color.Transparent,
                                    titleContentColor = Color.White,
                                    navigationIconContentColor = Color.White,
                                    actionIconContentColor = Color.White,
                                ),
                                modifier = Modifier.background(color = Color.Transparent),
                                windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp))
                        }) { innerPadding ->

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp)
                                .verticalScroll(scrollState),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                        ) {
                            args?.let { args ->
                                Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
                                CurrentWeatherScreen(weatherInfoViewModel)
                                HourlyForecastScreen(weatherInfoViewModel) {
                                    nestedRoutes = it
                                }
                                SimpleDailyForecastScreen(weatherInfoViewModel, navigate = {
                                    nestedRoutes = it
                                })
                                SimpleMapScreen(args)
                                AirQualityScreen(args)
                                SimpleSunSetRiseScreen(args)
                                FlickrImageItemScreen(weatherInfoViewModel.flickrRequestParameter) {
                                    backgroundImageUrl = it
                                }
                                Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
                            }

                        }
                    }
                }

            }

            if (!enabledLocation) {
                UnavailableFeatureScreen(title = io.github.pknujsp.weatherwizard.core.common.R.string.title_location_is_disabled,
                    unavailableFeature = UnavailableFeature.LOCATION_SERVICE_DISABLED) {
                    openLocationSettings = true
                }
                if (openLocationSettings) {
                    gpsLocationManager.OpenSettingsForLocation {
                        reload++
                        openLocationSettings = false
                    }
                }
            }

            if (onClickedWeatherProviderButton) {
                args?.run {
                    WeatherProviderDialog(navigationBarHeight, weatherDataProvider) {
                        onClickedWeatherProviderButton = false
                        it?.let {
                            if (weatherDataProvider != it) {
                                weatherInfoViewModel.updateWeatherDataProvider(it)
                                reload++
                            }
                        }
                    }
                }
            }
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
                CompareHourlyForecastScreen(it) {
                    nestedRoutes = NestedWeatherRoutes.Main
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeatherProviderDialog(navigationBarHeight: Dp, currentProvider: WeatherDataProvider, onClick: (WeatherDataProvider?) -> Unit) {
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