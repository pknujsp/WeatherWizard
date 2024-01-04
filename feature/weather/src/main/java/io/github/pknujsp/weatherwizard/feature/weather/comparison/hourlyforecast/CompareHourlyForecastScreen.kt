package io.github.pknujsp.weatherwizard.feature.weather.comparison.hourlyforecast

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.common.util.AStyle
import io.github.pknujsp.weatherwizard.core.common.util.toAnnotated
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.onLoading
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.CompareHourlyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastComparisonReport
import io.github.pknujsp.weatherwizard.core.ui.DynamicDateTime
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithoutNavigation
import io.github.pknujsp.weatherwizard.core.ui.lottie.CancellableLoadingScreen
import io.github.pknujsp.weatherwizard.core.ui.theme.AppShapes
import io.github.pknujsp.weatherwizard.core.resource.R


@Composable
fun CompareHourlyForecastScreen(
    args: RequestWeatherArguments, viewModel: CompareHourlyForecastViewModel = hiltViewModel(), popBackStack: () -> Unit
) {
    BackHandler {
        popBackStack()
    }

    LaunchedEffect(Unit) {
        viewModel.load(args)
    }

    val hourlyForecast by viewModel.hourlyForecast.collectAsStateWithLifecycle()
    val hourlyForecastComparisonReport by viewModel.report.collectAsStateWithLifecycle()
    val compareForecastCard = remember {
        CompareForecastCard()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
    ) {
        TitleTextWithNavigation(title = stringResource(id = R.string.title_comparison_hourly_forecast)) {
            popBackStack()
        }
        Column(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .verticalScroll(rememberScrollState())) {
            compareForecastCard.CompareCardSurface {
                hourlyForecast.onLoading {
                    CancellableLoadingScreen(stringResource(id = R.string.loading_daily_forecast_data)) {
                        popBackStack()
                    }
                }.onSuccess {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(vertical = 16.dp)) {
                        val mainLazyListState = rememberLazyListState()
                        DynamicDateTime(it.dateTimeInfo, mainLazyListState)
                        Content(it, mainLazyListState)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            ReportScreen(hourlyForecastComparisonReport)
        }
    }
}

@Composable
fun Content(compareHourlyForecastInfo: CompareHourlyForecastInfo, lazyListState: LazyListState) {
    val itemsCount = compareHourlyForecastInfo.items.size
    val itemModifier = Modifier.width(CompareHourlyForecastInfo.itemWidth)
    val context = LocalContext.current
    val weatherDataProviderInfoHeight = 36.dp
    val weatherDataProviderInfoHeightPx = with(LocalDensity.current) {
        weatherDataProviderInfoHeight.toPx().toInt()
    }

    Layout(
        content = {
            LazyRow(
                modifier = Modifier
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
internal fun WeatherDataProviderInfo(weatherProvider: WeatherProvider, height: Dp) {
    Row(horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(height)
            .padding(start = 12.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(weatherProvider.icon).crossfade(false).build(),
            contentDescription = stringResource(id = R.string.weather_provider),
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(id = weatherProvider.title),
            fontSize = 15.sp,
            color = Color.White,
        )
    }
}

@Composable
private fun Item(
    forecast: CompareHourlyForecast.Item, modifier: Modifier, onClick: (String) -> Unit
) {
    // 시각, 아이콘, 강수확률, 강수량
    forecast.run {
        val weatherConditionText = stringResource(id = weatherCondition)
        Column(
            modifier = Modifier
                .then(modifier)
                .clickable { onClick(weatherConditionText) },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 시각
            Text(text = hour, style = TextStyle(fontSize = 13.sp, color = Color.White))
            // 아이콘
            AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(weatherIcon).crossfade(false).build(),
                contentDescription = weatherConditionText,
                modifier = Modifier
                    .padding(4.dp)
                    .size(32.dp))

            Text(text = temperature, style = TextStyle(fontSize = 13.sp, color = Color.White))
        }
    }
}

@Composable
private fun ReportScreen(uiState: UiState<HourlyForecastComparisonReport>) {
    uiState.onSuccess { model ->
        Column {
            TitleTextWithoutNavigation(title = stringResource(id = R.string.title_comparison_report))
            Row(verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.horizontalScroll(rememberScrollState())) {
                model.commonForecasts.forEach { entry ->
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        val aStyle = listOf(AStyle(
                            contentId = listOf("icon" to InlineTextContent(Placeholder(width = 30.sp,
                                height = 28.sp,
                                placeholderVerticalAlign = PlaceholderVerticalAlign.Center)) {
                                AsyncImage(model = ImageRequest.Builder(LocalContext.current)
                                    .data(entry.value.weatherConditionCategory.dayWeatherIcon).crossfade(false).build(),
                                    contentDescription = null)
                            }),
                        ),
                            AStyle(
                                text = stringResource(id = entry.key.stringRes),
                            ))


                        Text(text = aStyle.toAnnotated(),
                            style = TextStyle(fontSize = 20.sp, color = Color.Black, fontWeight = FontWeight.Bold),
                            inlineContent = aStyle.first().inlineContents)

                        val times = entry.value.times.map {
                            listOf(
                                AStyle(
                                    text = "${it.first}\n",
                                    span = SpanStyle(fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Normal),
                                ),
                            ) + it.second.map { time ->
                                AStyle(
                                    text = " ${time}\n",
                                    span = SpanStyle(fontSize = 15.sp, color = Color.Gray, fontWeight = FontWeight.Normal),
                                )
                            }
                        }.flatten().toAnnotated()

                        Text(text = times)
                    }
                }
            }
        }
    }
}

@Stable
class CompareForecastCard {
    private val surfaceModifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = 12.dp)
    private val backgroundColor = Color(107, 107, 107, 217)

    @Composable
    fun CompareCardSurface(content: @Composable () -> Unit) {
        Surface(
            modifier = surfaceModifier,
            shape = AppShapes.large,
            color = backgroundColor,
        ) {
            content()
        }
    }
}