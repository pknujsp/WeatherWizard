package io.github.pknujsp.everyweather.feature.weather.info

import androidx.annotation.StringRes
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import io.github.pknujsp.everyweather.core.ads.AdMob
import io.github.pknujsp.everyweather.core.common.util.AStyle
import io.github.pknujsp.everyweather.core.common.util.toAnnotated
import io.github.pknujsp.everyweather.core.model.settings.CurrentUnits
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
import io.github.pknujsp.everyweather.feature.weather.info.geocode.TopAppBarUiState
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.simple.HourlyForecastScreen
import io.github.pknujsp.everyweather.feature.weather.route.NestedWeatherRoutes
import io.github.pknujsp.everyweather.feature.weather.summary.SummaryScreen
import kotlinx.coroutines.launch

private val DEFAULT_PADDING = 12.dp
private val COLUMN_ITEM_SPACING = 20.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherContentScreen(
    scrollState: ScrollState,
    scrollBehavior: TopAppBarScrollBehavior,
    navigate: (NestedWeatherRoutes) -> Unit,
    reload: () -> Unit,
    updateWeatherDataProvider: (WeatherProvider) -> Unit,
    uiState: WeatherContentUiState.Success,
    openDrawer: () -> Unit,
    topAppBarUiState: TopAppBarUiState,
) {
    var onClickedWeatherProviderButton by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var imageUrl by remember { mutableStateOf("") }
    val weather = uiState.weather
    var showAiSummary by remember { mutableStateOf(false) }

    AsyncImage(
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
        model = ImageRequest.Builder(LocalContext.current).diskCachePolicy(CachePolicy.ENABLED).crossfade(250).data(imageUrl).build(),
        contentDescription = stringResource(R.string.background_image),
    )

    Scaffold(containerColor = Color.Black.copy(alpha = 0.1f), topBar = {
        TopAppBars(
            topAppBarUiState = topAppBarUiState,
            weatherContentUiState = uiState,
            openDrawer = openDrawer,
            reload = reload,
            summarize = {
                showAiSummary = true
            },
            onClickedWeatherProviderButton = { onClickedWeatherProviderButton = true },
            scrollBehavior = scrollBehavior,
        )
    }) { _ ->
        val systemBars = WindowInsets.systemBars
        val density = LocalDensity.current
        val bottomPadding = remember { with(density) { systemBars.getBottom(this).toDp() } }
        val localConfiguration = LocalConfiguration.current

        val screenHeight = remember {
            with(density) {
                localConfiguration.screenHeightDp + (systemBars.getBottom(this) + systemBars.getTop(this)) / this.density
            }.dp
        }

        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = DEFAULT_PADDING)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(COLUMN_ITEM_SPACING),
            ) {
                Box(modifier = Modifier.height(screenHeight - DEFAULT_PADDING), contentAlignment = Alignment.BottomStart) {
                    Column(verticalArrangement = Arrangement.spacedBy(COLUMN_ITEM_SPACING)) {
                        CurrentWeatherScreen(weather.currentWeather, weather.yesterdayWeather)
                        HourlyForecastScreen(hourlyForecast = weather.simpleHourlyForecast, navigate = navigate)
                    }
                }
                SimpleDailyForecastScreen(weather.simpleDailyForecast, navigate)
                SimpleMapScreen(uiState.args)
                AirQualityScreen(uiState.args, uiState.lastUpdatedDateTime, onLoadAirQuality = { aqi ->
                    weather.currentWeather.airQuality = aqi.current.aqi
                    uiState.weatherEntities.airQuality = aqi
                })
                SimpleSunSetRiseScreen(SunSetRiseInfo(uiState.args.latitude, uiState.args.longitude, uiState.lastUpdatedDateTime))
                AdMob.BannerAd(modifier = Modifier.fillMaxWidth())
                FlickrImageItemScreen(requestParameter = uiState.weather.flickrRequestParameters, onImageUrlChanged = {
                    imageUrl = it
                })
                Footer(Modifier.align(Alignment.End), uiState.currentUnits)
                Spacer(modifier = Modifier.height(bottomPadding))
            }
            Box(modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(bottomPadding)
                .background(brush = shadowBox(ShadowDirection.UP)))
        }

        BottomSheetDialog(title = stringResource(id = R.string.title_weather_data_provider),
            selectedItem = uiState.args.weatherProvider,
            onSelectedItem = {
                coroutineScope.launch {
                    it?.let {
                        if (uiState.args.weatherProvider != it) {
                            updateWeatherDataProvider(it)
                        }
                    }
                }
            },
            enums = WeatherProvider.enums,
            expanded = { onClickedWeatherProviderButton },
            onDismissRequest = { onClickedWeatherProviderButton = false })

        if (showAiSummary) {
            SummaryScreen(model = uiState.weatherEntities, onDismiss = {
                showAiSummary = false
            })
        }
    }
}


/**
 * 풍속, 강수량 단위 표시
 */
@Composable
private fun Footer(modifier: Modifier = Modifier, units: CurrentUnits) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp), horizontalAlignment = Alignment.End) {
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