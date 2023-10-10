package io.github.pknujsp.weatherwizard.feature.weather.info

import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.common.GpsLocationManager
import io.github.pknujsp.weatherwizard.core.common.R
import io.github.pknujsp.weatherwizard.core.common.UnavailableFeature
import io.github.pknujsp.weatherwizard.core.common.util.AStyle
import io.github.pknujsp.weatherwizard.core.common.util.toAnnotated
import io.github.pknujsp.weatherwizard.core.model.ProcessState
import io.github.pknujsp.weatherwizard.core.model.onRunning
import io.github.pknujsp.weatherwizard.core.model.onSucceed
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherDataArgs
import io.github.pknujsp.weatherwizard.core.ui.UnavailableFeatureScreen
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
import io.github.pknujsp.weatherwizard.feature.weather.info.currentweather.simple.CurrentWeatherScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.dailyforecast.simple.SimpleDailyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.simple.HourlyForecastScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherContentScreen(arguments: ContentArguments,weatherInfoViewModel: WeatherInfoViewModel) {
    val gpsLocationManager = GpsLocationManager(LocalContext.current)
    var openLocationSettings by remember { mutableStateOf(false) }
    var onClickedWeatherProviderButton by remember { mutableStateOf(false) }
    val headInfo by weatherInfoViewModel.reverseGeoCode.collectAsStateWithLifecycle()

    Box {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            model = ImageRequest.Builder(LocalContext.current).run {
                crossfade(200)
                if (arguments.backgroundImageUrl.isEmpty()) data(R.drawable.bg_grad)
                else data(arguments.backgroundImageUrl)
                build()
            },
            contentDescription = stringResource(io.github.pknujsp.weatherwizard.feature.weather.R.string.background_image),
            filterQuality = FilterQuality.High,
        )

        arguments.processState.onRunning {
            NonCancellableLoadingScreen(stringResource(id = R.string.loading_weather_data)) {

            }
        }.onSucceed {
            Scaffold(modifier = Modifier.nestedScroll(arguments.scrollBehavior.nestedScrollConnection),
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
                                            .size(15.dp)
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
                                                    fontSize = 18.sp,
                                                ),
                                            ),
                                            AStyle(it.displayName, span = SpanStyle(fontSize = 24.sp)),
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
                                                .data(R.drawable.ic_time)
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
                                        arguments.run {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(args.weatherDataProvider.logo).crossfade(false).build(),
                                                contentDescription = stringResource(id = io.github.pknujsp.weatherwizard.feature.weather.R.string.weather_provider),
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
                            IconButton(onClick = { arguments.reload() }) {
                                Icon(painter = painterResource(id = R.drawable.ic_refresh),
                                    contentDescription = null)
                            }
                        },
                        scrollBehavior = arguments.scrollBehavior,
                        colors = CustomTopAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = Color.Transparent,
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White,
                            actionIconContentColor = Color.White,
                        ),
                        modifier = Modifier.background(brush = shardowBox()),
                        windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp))
                }) { innerPadding ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                        .verticalScroll(arguments.scrollState),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    arguments.run {
                        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
                        CurrentWeatherScreen(weatherInfoViewModel)
                        HourlyForecastScreen(weatherInfoViewModel, navigate)
                        SimpleDailyForecastScreen(weatherInfoViewModel, navigate)
                        SimpleMapScreen(args)
                        AirQualityScreen(args)
                        SimpleSunSetRiseScreen(args)
                        FlickrImageItemScreen(weatherInfoViewModel.flickrRequestParameter) {
                            onChangedBackgroundImageUrl(it)
                        }
                        Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
                    }

                }
            }
        }
    }

    if (!arguments.enabledLocation) {
        UnavailableFeatureScreen(title = R.string.title_location_is_disabled,
            unavailableFeature = UnavailableFeature.LOCATION_SERVICE_DISABLED) {
            openLocationSettings = true
        }
        if (openLocationSettings) {
            gpsLocationManager.OpenSettingsForLocation {
                arguments.reload()
                openLocationSettings = false
            }
        }
    }

    if (onClickedWeatherProviderButton) {
        WeatherProviderDialog(arguments.args.weatherDataProvider) {
            onClickedWeatherProviderButton = false
            it?.let {
                if (arguments.args.weatherDataProvider != it) {
                    weatherInfoViewModel.updateWeatherDataProvider(it)
                    arguments.reload()
                }
            }
        }
    }
}

@Stable
fun shardowBox(
): Brush = Brush.linearGradient(
    0.0f to Color.Black.copy(alpha = 0.5f),
    1.0f to Color.Transparent,
    start = Offset(0.0f, 0f),
    end = Offset(0.0f, Float.POSITIVE_INFINITY),
    tileMode = TileMode.Clamp
)

@Stable
class ContentArguments @OptIn(ExperimentalMaterial3Api::class) constructor(
    val args: RequestWeatherDataArgs,
    val scrollState: ScrollState,
    val scrollBehavior: TopAppBarScrollBehavior,
    val processState: ProcessState,
    val enabledLocation: Boolean,
    var backgroundImageUrl: String,
    val navigate: (NestedWeatherRoutes) -> Unit,
    val reload: () -> Unit,
    val onChangedBackgroundImageUrl: (String) -> Unit,
)