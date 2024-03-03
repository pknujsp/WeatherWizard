package io.github.pknujsp.everyweather.feature.weather.info

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import io.github.pknujsp.everyweather.core.ads.AdMob
import io.github.pknujsp.everyweather.core.common.util.AStyle
import io.github.pknujsp.everyweather.core.common.util.toAnnotated
import io.github.pknujsp.everyweather.core.model.settings.CurrentUnits
import io.github.pknujsp.everyweather.core.model.weather.common.AirQualityValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherDataUnit
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.BottomSheetDialog
import io.github.pknujsp.everyweather.core.ui.theme.ShadowDirection
import io.github.pknujsp.everyweather.core.ui.theme.outlineTextStyle
import io.github.pknujsp.everyweather.core.ui.theme.shadowBox
import io.github.pknujsp.everyweather.feature.airquality.AirQualityScreen
import io.github.pknujsp.everyweather.feature.flickr.FlickrImageItemScreen
import io.github.pknujsp.everyweather.feature.map.screen.SimpleMapScreen
import io.github.pknujsp.everyweather.feature.sunsetrise.SimpleSunSetRiseScreen
import io.github.pknujsp.everyweather.feature.sunsetrise.SunSetRiseInfo
import io.github.pknujsp.everyweather.feature.weather.info.currentweather.simple.CurrentWeatherScreen
import io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.simple.SimpleDailyForecastScreen
import io.github.pknujsp.everyweather.feature.weather.info.geocode.TargetLocationViewModel
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.simple.HourlyForecastScreen
import io.github.pknujsp.everyweather.feature.weather.route.NestedWeatherRoutes
import io.github.pknujsp.everyweather.feature.weather.summary.SummaryScreen
import kotlinx.coroutines.launch

private val DEFAULT_PADDING = 12.dp
private val COLUMN_ITEM_SPACING = 20.dp

@Composable
fun WeatherInfoScreen(
    refresh: () -> Unit,
    navigate: (NestedWeatherRoutes) -> Unit,
    openDrawer: () -> Unit,
    weatherContentUiState: WeatherContentUiState.Success,
    viewModel: WeatherInfoViewModel = hiltViewModel(),
    targetLocationViewModel: TargetLocationViewModel = hiltViewModel(),
) {
    val currentOpenDrawer by rememberUpdatedState(openDrawer)
    val currentNavigate by rememberUpdatedState(navigate)
    val currentRefresh by rememberUpdatedState(refresh)

    val topAppBarUiState = targetLocationViewModel.topAppBarUiState
    var onClickedWeatherProviderButton by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var imageUrl by remember { mutableStateOf("") }
    var showAiSummary by remember { mutableStateOf(false) }
    val weather = weatherContentUiState.weather

    val systemBars = WindowInsets.systemBars
    val density = LocalDensity.current
    val systemBarPadding: PaddingValues = remember {
        with(density) {
            PaddingValues(top = systemBars.getTop(this).toDp(), bottom = systemBars.getBottom(this).toDp(), start = 0.dp, end = 0.dp)
        }
    }

    LaunchedEffect(weatherContentUiState) {
        targetLocationViewModel.setLocation(weatherContentUiState.args.targetLocation)
    }

    val scrollState = rememberScrollState()
    val flingBehavior = ScrollableDefaults.flingBehavior()

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            model = ImageRequest.Builder(LocalContext.current).diskCachePolicy(CachePolicy.ENABLED).crossfade(200).data(imageUrl).build(),
            contentDescription = stringResource(R.string.background_image),
        )

        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.11f), RectangleShape)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = DEFAULT_PADDING)
                    .verticalScroll(scrollState, flingBehavior = flingBehavior),
                verticalArrangement = Arrangement.spacedBy(COLUMN_ITEM_SPACING),
            ) {
                val localConfiguration = LocalConfiguration.current
                val screenHeight = remember {
                    (localConfiguration.screenHeightDp + (systemBarPadding.calculateTopPadding() + systemBarPadding.calculateBottomPadding()).value).dp
                }
                var currentAirQuality by remember { mutableStateOf<AirQualityValueType?>(null) }

                Box(modifier = Modifier.height(screenHeight - DEFAULT_PADDING), contentAlignment = Alignment.BottomStart) {
                    Column(verticalArrangement = Arrangement.spacedBy(COLUMN_ITEM_SPACING)) {
                        CurrentWeatherScreen(weather.currentWeather) { currentAirQuality }
                        HourlyForecastScreen(hourlyForecast = weather.simpleHourlyForecast, navigate = currentNavigate)
                    }
                }
                SimpleDailyForecastScreen(weather.simpleDailyForecast, currentNavigate)
                SimpleMapScreen(weatherContentUiState.args)
                AirQualityScreen(weatherContentUiState.args, weatherContentUiState.lastUpdatedDateTime, onLoadAirQuality = { aqi ->
                    coroutineScope.launch {
                        currentAirQuality = aqi.current.aqi
                        weatherContentUiState.weatherEntities.airQuality = aqi
                    }
                })
                SimpleSunSetRiseScreen(SunSetRiseInfo(weatherContentUiState.args.targetLocation.latitude,
                    weatherContentUiState.args.targetLocation.longitude,
                    weatherContentUiState.lastUpdatedDateTime))
                AdMob.BannerAd(modifier = Modifier.fillMaxWidth())
                FlickrImageItemScreen(requestParameter = weather.flickrRequestParameters, onImageUrlChanged = {
                    coroutineScope.launch {
                        imageUrl = it
                    }
                })
                Footer(units = weatherContentUiState.currentUnits)
                Spacer(modifier = Modifier.height(systemBarPadding.calculateBottomPadding()))
            }

            TopAppBar(
                modifier = Modifier.align(Alignment.TopCenter),
                topAppBarUiState = topAppBarUiState,
                weatherContentUiState = weatherContentUiState,
                openDrawer = currentOpenDrawer,
                reload = {
                    coroutineScope.launch {
                        currentRefresh()
                    }
                },
                summarize = {
                    showAiSummary = true
                },
                onClickedWeatherProviderButton = { onClickedWeatherProviderButton = true },
                scrollState = scrollState,
                flingBehavior = flingBehavior,
            )

            Box(modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(systemBarPadding.calculateBottomPadding())
                .background(brush = shadowBox(ShadowDirection.UP)))
        }

        BottomSheetDialog(title = stringResource(id = R.string.title_weather_data_provider),
            selectedItem = weatherContentUiState.args.weatherProvider,
            onSelectedItem = {
                if (it != null && weatherContentUiState.args.weatherProvider != it) {
                    viewModel.replaceWeatherProvider(it)
                }
            },
            enums = WeatherProvider.enums,
            expanded = { onClickedWeatherProviderButton },
            onDismissRequest = { onClickedWeatherProviderButton = false })

        if (showAiSummary) {
            SummaryScreen(model = weatherContentUiState.weatherEntities, onDismiss = {
                showAiSummary = false
            })
        }
    }
}

/**
 * 풍속, 강수량 단위 표시
 */
@Composable
private fun ColumnScope.Footer(modifier: Modifier = Modifier, units: CurrentUnits) {
    Column(modifier = modifier.align(Alignment.End),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.End) {
        UnitItem(title = R.string.title_wind_speed_unit, unit = units.windSpeedUnit)
        UnitItem(title = R.string.title_precipitation_unit, unit = units.precipitationUnit)
    }
}

@Composable
private fun UnitItem(modifier: Modifier = Modifier, @StringRes title: Int, unit: WeatherDataUnit) {
    Text(text = listOf(AStyle(text = "${stringResource(id = title)} : ", span = SpanStyle(fontSize = 13.sp, color = Color.White)),
        AStyle(text = unit.symbol, span = SpanStyle(fontSize = 13.sp, color = Color.White))).toAnnotated(),
        style = LocalTextStyle.current.merge(outlineTextStyle))
}