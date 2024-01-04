package io.github.pknujsp.weatherwizard.feature.weather.info

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.BottomSheetDialog
import io.github.pknujsp.weatherwizard.core.ui.theme.ShadowDirection
import io.github.pknujsp.weatherwizard.core.ui.theme.shadowBox
import io.github.pknujsp.weatherwizard.feature.airquality.AirQualityScreen
import io.github.pknujsp.weatherwizard.feature.flickr.FlickrImageItemScreen
import io.github.pknujsp.weatherwizard.feature.map.SimpleMapScreen
import io.github.pknujsp.weatherwizard.feature.sunsetrise.SimpleSunSetRiseScreen
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
    )

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color.Black.copy(alpha = 0.1f),
        topBar = {
            TopAppBars(
                uiState = uiState,
                openDrawer = openDrawer,
                reload = reload,
                onClickedWeatherProviderButton = { onClickedWeatherProviderButton = true },
                scrollBehavior = scrollBehavior,
            )
        }) { _ ->
        val systemBarsPadding = with(LocalDensity.current) {
            PaddingValues(top = 200.dp, bottom = WindowInsets.navigationBars.getBottom(this).toDp())
        }
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
                    .verticalScroll(scrollState)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                weather.run {
                    Spacer(modifier = Modifier.height(systemBarsPadding.calculateTopPadding()))
                    CurrentWeatherScreen(currentWeather, yesterdayWeather)
                    HourlyForecastScreen(simpleHourlyForecast, navigate)
                    SimpleDailyForecastScreen(simpleDailyForecast, navigate)
                    SimpleMapScreen(uiState.args)
                    AirQualityScreen(uiState.args)
                    SimpleSunSetRiseScreen(uiState.args)
                    FlickrImageItemScreen(requestParameter = uiState.weather.flickrRequestParameters, onImageUrlChanged = {
                        imageUrl = it
                    })
                    Spacer(modifier = Modifier.height(systemBarsPadding.calculateBottomPadding()))
                }
            }
            Box(modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(systemBarsPadding.calculateBottomPadding())
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
    }
}