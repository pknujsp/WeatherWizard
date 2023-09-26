package io.github.pknujsp.weatherwizard.feature.weather.info


import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.common.GpsLocationManager
import io.github.pknujsp.weatherwizard.core.common.util.AStyle
import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.toAnnotated
import io.github.pknujsp.weatherwizard.core.model.favorite.TargetAreaType
import io.github.pknujsp.weatherwizard.core.model.onLoading
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.ui.lottie.CancellableLoadingScreen
import io.github.pknujsp.weatherwizard.core.ui.theme.notIncludeTextPaddingStyle
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherBackgroundPlaceHolder
import io.github.pknujsp.weatherwizard.feature.airquality.AirQualityScreen
import io.github.pknujsp.weatherwizard.feature.flickr.FlickrImageItemScreen
import io.github.pknujsp.weatherwizard.feature.map.SimpleMapScreen
import io.github.pknujsp.weatherwizard.feature.sunsetrise.SimpleSunSetRiseScreen
import io.github.pknujsp.weatherwizard.feature.weather.CustomTopAppBar
import io.github.pknujsp.weatherwizard.feature.weather.CustomTopAppBarColors
import io.github.pknujsp.weatherwizard.feature.weather.info.currentweather.simple.CurrentWeatherScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.dailyforecast.SimpleDailyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.simple.HourlyForecastScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WeatherInfoScreen() {
    val weatherInfoViewModel: WeatherInfoViewModel = hiltViewModel()
    val args by weatherInfoViewModel.requestArgs.collectAsStateWithLifecycle()
    val backgroundImageUrl by remember { derivedStateOf { mutableStateOf("") } }
    var currentDayOrNight by remember { mutableStateOf(DayNightCalculator.DayNight.NIGHT) }
    val weatherInfo by weatherInfoViewModel.weatherDataState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val headInfo by weatherInfoViewModel.reverseGeoCode.collectAsStateWithLifecycle()

    val view = LocalView.current
    val insetController = remember {
        (view.context as Activity).window.run {
            WindowCompat.getInsetsController(this, decorView)
        }
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            weatherInfoViewModel.targetAreaType.collect { typeUiState ->
                typeUiState.onSuccess {
                    if (it is TargetAreaType.CurrentLocation) {
                        val result = GpsLocationManager(context).getCurrentLocation()
                        if (result is GpsLocationManager.CurrentLocationResult.Success) weatherInfoViewModel.setArgs(result.location)
                    }
                }
            }
        }
    }

    Scaffold(modifier = Modifier
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color.Transparent,
        topBar = {
            CustomTopAppBar(smallTitle = {
                Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center) {
                    headInfo.onSuccess {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Rounded.Place,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier
                                    .size(14.dp)
                                    .padding(end = 4.dp))
                            Text(
                                text = it.displayName, color = Color.Black, fontSize = 14.sp, style = LocalTextStyle.current.merge
                                    (notIncludeTextPaddingStyle)
                            )
                        }
                        Text(
                            text = it.requestDateTime,
                            fontSize = TextUnit(11f, TextUnitType.Sp),
                            color = Color.Black, style = LocalTextStyle.current.merge
                                (notIncludeTextPaddingStyle)
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
                        headInfo.onSuccess {
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
                                color = Color.Black,
                                lineHeight = 28.sp,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(io.github.pknujsp.weatherwizard.core.common.R.drawable.ic_time).crossfade(false)
                                        .build(),
                                    contentDescription = stringResource(id = io.github.pknujsp.weatherwizard.core.model.R.string.weather_info_head_info_update_time),
                                    colorFilter = ColorFilter.tint(Color.Black),
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = it.requestDateTime,
                                    fontSize = TextUnit(14f, TextUnitType.Sp),
                                    color = Color.Black,
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { weatherInfoViewModel.reload() }) {
                        Icon(painter = painterResource(id = io.github.pknujsp.weatherwizard.core.common.R.drawable.ic_refresh),
                            contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = CustomTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black,
                    actionIconContentColor = Color.Black,
                ),
                modifier = Modifier.background(color = Color.White),
                windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp))
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            weatherInfo.onLoading {
                CancellableLoadingScreen(stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.loading_weather_data)) {}
                args.onSuccess {
                    weatherInfoViewModel.loadAllWeatherData(it)
                }
            }.onSuccess {
                args.onSuccess { requestWeatherDataArgs ->
                    Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding() + 24.dp))
                    CurrentWeatherScreen(weatherInfoViewModel, backgroundImageUrl.value)
                    HourlyForecastScreen(weatherInfoViewModel)
                    SimpleDailyForecastScreen(weatherInfoViewModel)
                    SimpleMapScreen { weatherInfoViewModel.requestArgs }
                    AirQualityScreen(requestWeatherDataArgs)
                    SimpleSunSetRiseScreen(requestWeatherDataArgs.latitude, requestWeatherDataArgs.longitude) {
                        currentDayOrNight = it
                    }
                    FlickrImageItemScreen({ weatherInfoViewModel.flickrRequestParameter }) {
                        backgroundImageUrl.value = it
                    }
                    Spacer(modifier = Modifier.height(56.dp))
                }
            }
        }
    }


}


@Composable
private fun PlaceHolder() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)
        .wrapContentHeight()) {
        io.github.pknujsp.weatherwizard.core.ui.PlaceHolder(modifier = Modifier
            .size(90.dp)
            .align(Alignment.BottomStart))

        io.github.pknujsp.weatherwizard.core.ui.PlaceHolder(modifier = Modifier
            .size(60.dp)
            .align(Alignment.BottomEnd))
    }

    io.github.pknujsp.weatherwizard.core.ui.PlaceHolder(modifier = Modifier
        .fillMaxWidth()
        .height(13.dp)
        .padding(start = 84.dp, end = 14.dp, top = 6.dp))

    SimpleWeatherBackgroundPlaceHolder()
    SimpleWeatherBackgroundPlaceHolder()
    SimpleWeatherBackgroundPlaceHolder()
}