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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.common.util.AStyle
import io.github.pknujsp.weatherwizard.core.common.util.toAnnotated
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.theme.ShadowDirection
import io.github.pknujsp.weatherwizard.core.ui.theme.notIncludeTextPaddingStyle
import io.github.pknujsp.weatherwizard.core.ui.theme.outlineTextStyle
import io.github.pknujsp.weatherwizard.core.ui.theme.shadowBox
import io.github.pknujsp.weatherwizard.feature.airquality.AirQualityScreen
import io.github.pknujsp.weatherwizard.feature.flickr.FlickrImageItemScreen
import io.github.pknujsp.weatherwizard.feature.map.SimpleMapScreen
import io.github.pknujsp.weatherwizard.feature.sunsetrise.SimpleSunSetRiseScreen
import io.github.pknujsp.weatherwizard.feature.weather.CustomTopAppBar
import io.github.pknujsp.weatherwizard.feature.weather.CustomTopAppBarColors
import io.github.pknujsp.weatherwizard.feature.weather.info.currentweather.simple.CurrentWeatherScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.dailyforecast.simple.SimpleDailyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.simple.HourlyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.route.NestedWeatherRoutes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherContentScreen(
    scrollState: ScrollState,
    scrollBehavior: TopAppBarScrollBehavior,
    navigate: (NestedWeatherRoutes) -> Unit,
    reload: () -> Unit,
    updateWeatherDataProvider: (WeatherProvider) -> Unit,
    updateWindowInset: () -> Unit,
    uiState: WeatherContentUiState.Success,
    openDrawer: () -> Unit,
) {
    var onClickedWeatherProviderButton by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var imageUrl by remember { mutableStateOf("") }
    val weather = uiState.weather

    LaunchedEffect(imageUrl) {
        if (imageUrl.isNotEmpty()) {
            updateWindowInset()
        }
    }

    AsyncImage(
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
        model = ImageRequest.Builder(LocalContext.current).crossfade(200).data(imageUrl).build(),
        contentDescription = stringResource(R.string.background_image),
        filterQuality = FilterQuality.High,
    )

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color.Black.copy(alpha = 0.12f),
        topBar = {
            CustomTopAppBar(smallTitle = {
                Column(modifier = Modifier.statusBarsPadding(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Rounded.Place,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(15.dp)
                                .padding(end = 4.dp))
                        Text(
                            text = uiState.args.location.address,
                            color = Color.White,
                            fontSize = 14.sp,
                            style = LocalTextStyle.current.merge(notIncludeTextPaddingStyle).merge(outlineTextStyle),
                        )
                    }
                    Text(
                        text = uiState.dateTime.toString(),
                        fontSize = TextUnit(11f, TextUnitType.Sp),
                        color = Color.White,
                        style = LocalTextStyle.current.merge(notIncludeTextPaddingStyle).merge(outlineTextStyle),
                    )
                }
            },
                bigTitle = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 62.dp),
                    ) {
                        Text(
                            text = listOf(
                                AStyle(
                                    "${uiState.args.location.country}\n",
                                    span = SpanStyle(
                                        fontSize = 17.sp,
                                    ),
                                ),
                                AStyle(uiState.args.location.address, span = SpanStyle(fontSize = 24.sp)),
                            ).toAnnotated(),
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 28.sp,
                            style = LocalTextStyle.current.merge(outlineTextStyle),
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current).data(R.drawable.ic_time).crossfade(false).build(),
                                contentDescription = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.weather_info_head_info_update_time),
                                colorFilter = ColorFilter.tint(Color.White),
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = uiState.dateTime.toString(),
                                fontSize = 14.sp,
                                color = Color.White, style = LocalTextStyle.current.merge(outlineTextStyle),
                            )
                        }
                        Row(horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                onClickedWeatherProviderButton = true
                            }) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current).data(uiState.args.weatherProvider.icon).crossfade(false)
                                    .build(),
                                contentDescription = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.weather_provider),
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(id = uiState.args.weatherProvider.title),
                                fontSize = 14.sp,
                                color = Color.White,
                                style = LocalTextStyle.current.merge(outlineTextStyle),
                            )

                        }
                    }
                },
                actions = {
                    IconButton(modifier = Modifier.statusBarsPadding(), onClick = { reload() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_refresh), contentDescription = null)
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
                modifier = Modifier.background(brush = shadowBox()),
                windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
                navigationIcon = {
                    IconButton(modifier = Modifier.statusBarsPadding(), onClick = openDrawer) {
                        Icon(Icons.Rounded.Menu, contentDescription = null)
                    }
                })
        }) { innerPadding ->
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                weather.run {
                    Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
                    CurrentWeatherScreen(currentWeather, yesterdayWeather)
                    HourlyForecastScreen(simpleHourlyForecast, navigate)
                    SimpleDailyForecastScreen(simpleDailyForecast, navigate)
                    SimpleMapScreen(uiState.args)
                    AirQualityScreen(uiState.args)
                    SimpleSunSetRiseScreen(uiState.args)
                    FlickrImageItemScreen(requestParameter = uiState.weather.flickrRequestParameters, onImageUrlChanged = {
                        coroutineScope.launch {
                            imageUrl = it
                        }
                    })
                    Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
                }
            }
            Box(modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(innerPadding.calculateBottomPadding())
                .background(brush = shadowBox(ShadowDirection.UP)))
        }

        if (onClickedWeatherProviderButton) {
            WeatherProviderDialog(uiState.args.weatherProvider) {
                onClickedWeatherProviderButton = false
                it?.let {
                    if (uiState.args.weatherProvider != it) {
                        updateWeatherDataProvider(it)
                        reload()
                    }
                }
            }
        }


    }
}