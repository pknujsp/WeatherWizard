package io.github.pknujsp.everyweather.feature.weather.comparison.hourlyforecast

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.everyweather.core.model.UiState
import io.github.pknujsp.everyweather.core.model.onLoading
import io.github.pknujsp.everyweather.core.model.onSuccess
import io.github.pknujsp.everyweather.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.ModalBottomSheetDialog
import io.github.pknujsp.everyweather.core.ui.TitleTextWithNavigation
import io.github.pknujsp.everyweather.core.ui.lottie.CancellableLoadingScreen
import io.github.pknujsp.everyweather.core.ui.time.DynamicDateTime
import io.github.pknujsp.everyweather.feature.weather.comparison.common.CommonForecastItemsScreen
import io.github.pknujsp.everyweather.feature.weather.comparison.common.CompareForecastCard
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model.CompareHourlyForecast
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model.HourlyForecastComparisonReport

@Composable
fun CompareHourlyForecastScreen(
    args: RequestWeatherArguments,
    viewModel: CompareHourlyForecastViewModel = hiltViewModel(),
    popBackStack: () -> Unit,
) {
    LaunchedEffect(args) {
        viewModel.load(args)
    }

    val hourlyForecast by viewModel.hourlyForecast.collectAsStateWithLifecycle()
    val hourlyForecastComparisonReport by viewModel.report.collectAsStateWithLifecycle()

    ModalBottomSheetDialog(freeHeight = true,
        title = stringResource(id = R.string.title_comparison_hourly_forecast),
        onDismiss = popBackStack) {
        Column(
            modifier =
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            hourlyForecast.onLoading {
                CancellableLoadingScreen(stringResource(id = R.string.loading_hourly_forecast_data)) {
                    popBackStack()
                }
            }.onSuccess {
                CompareForecastCard.CompareCardSurface {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(vertical = 16.dp)) {
                        val mainLazyListState = rememberLazyListState()
                        DynamicDateTime(it.dateTimeInfo, mainLazyListState)
                        Content(it, mainLazyListState)
                    }
                }
            }

            if (hourlyForecastComparisonReport is UiState.Success) {
                CommonForecastItemsScreen(
                    (hourlyForecastComparisonReport as UiState.Success<HourlyForecastComparisonReport>).data.commonForecasts,
                )
            }
        }
    }
}

@Composable
fun Content(
    compareHourlyForecastInfo: CompareHourlyForecastInfo,
    lazyListState: LazyListState,
) {
    val itemsCount = compareHourlyForecastInfo.items.size
    val itemModifier = remember { Modifier.width(CompareHourlyForecastInfo.itemWidth) }
    val context = LocalContext.current
    val weatherDataProviderInfoHeight = 36.dp
    val weatherDataProviderInfoHeightPx =
        with(LocalDensity.current) {
            weatherDataProviderInfoHeight.toPx().toInt()
        }

    Layout(
        content = {
            LazyRow(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .wrapContentHeight(),
                state = lazyListState,
            ) {
                items(count = itemsCount, key = { it }) { i ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(weatherDataProviderInfoHeight),
                    ) {
                        compareHourlyForecastInfo.items[i].forEach {
                            Item(it, itemModifier) { weatherCondition ->
                                Toast.makeText(context, weatherCondition, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            compareHourlyForecastInfo.weatherDataProviders.forEach {
                WeatherDataProviderInfo(it, weatherDataProviderInfoHeight)
            }
        },
        modifier = Modifier.fillMaxWidth(),
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        val providersCount = compareHourlyForecastInfo.weatherDataProviders.size
        val forecastRowHeight = (placeables[0].height - weatherDataProviderInfoHeightPx * (providersCount - 1)) / providersCount
        val height = placeables[0].height + weatherDataProviderInfoHeightPx

        layout(constraints.maxWidth, height) {
            placeables.first().run {
                placeRelative(0, weatherDataProviderInfoHeightPx)
            }
            placeables.drop(1).forEachIndexed { index, placeable ->
                placeable.run {
                    placeRelative(0, (weatherDataProviderInfoHeightPx + forecastRowHeight) * index)
                }
            }
        }
    }
}

@Composable
internal fun WeatherDataProviderInfo(
    weatherProvider: WeatherProvider,
    height: Dp,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
        Modifier
            .height(height)
            .padding(start = 12.dp),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(weatherProvider.icon).crossfade(false).build(),
            contentDescription = stringResource(id = R.string.weather_provider),
            modifier =
            Modifier
                .size(height)
                .padding(4.dp),
        )
        Text(
            text = stringResource(id = weatherProvider.title),
            fontSize = 14.sp,
            color = Color.White,
        )
    }
}

@Composable
private fun Item(
    forecast: CompareHourlyForecast.Item,
    modifier: Modifier,
    onClick: (String) -> Unit,
) {
    // 시각, 아이콘, 강수확률, 강수량
    forecast.run {
        val weatherConditionText = stringResource(id = weatherCondition)
        Column(
            modifier =
            Modifier
                .then(modifier)
                .clickable { onClick(weatherConditionText) },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 시각
            Text(text = hour, style = TextStyle(fontSize = 13.sp, color = Color.White))
            // 아이콘
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(weatherIcon).crossfade(false).build(),
                contentDescription = weatherConditionText,
                modifier =
                Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
            )

            Text(text = temperature, style = TextStyle(fontSize = 13.sp, color = Color.White))
        }
    }
}