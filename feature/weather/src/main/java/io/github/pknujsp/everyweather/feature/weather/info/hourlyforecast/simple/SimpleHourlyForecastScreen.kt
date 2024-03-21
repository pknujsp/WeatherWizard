package io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.simple

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.time.DynamicDateTime
import io.github.pknujsp.everyweather.core.ui.weather.item.CardInfo
import io.github.pknujsp.everyweather.core.ui.weather.item.SimpleWeatherScreenBackground
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model.SimpleHourlyForecast
import io.github.pknujsp.everyweather.feature.weather.info.ui.HourlyForecastItem
import io.github.pknujsp.everyweather.feature.weather.route.NestedRoutes

@Composable
fun HourlyForecastScreen(
    modifier: Modifier = Modifier,
    hourlyForecast: SimpleHourlyForecast,
    navigate: (NestedRoutes) -> Unit,
) {
    val currentNavigate by rememberUpdatedState(newValue = navigate)
    val lazyListState = rememberLazyListState()
    LaunchedEffect(hourlyForecast) {
        lazyListState.scrollToItem(0, 0)
    }

    SimpleWeatherScreenBackground(
        modifier = modifier,
        cardInfo =
        CardInfo(
            title = stringResource(id = R.string.hourly_forecast),
            buttons =
            listOf(
                stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.comparison) to {
                    currentNavigate(NestedRoutes.COMP_HOURLY_FORECAST)
                },
                stringResource(id = R.string.detail) to {
                    currentNavigate(NestedRoutes.DETAIL_HOURLY_FORECAST)
                },
            ),
            content = {
                DynamicDateTime(hourlyForecast.dateTimeInfo, lazyListState)
                HourlyForecastItem(simpleHourlyForecast = hourlyForecast, lazyListState)
            },
        ),
    )
}