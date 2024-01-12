package io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.simple

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.SimpleHourlyForecast
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.DynamicDateTime
import io.github.pknujsp.weatherwizard.core.ui.weather.item.CardInfo
import io.github.pknujsp.weatherwizard.core.ui.weather.item.HourlyForecastItem
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherScreenBackground
import io.github.pknujsp.weatherwizard.feature.weather.route.NestedWeatherRoutes


@Composable
fun HourlyForecastScreen(
    modifier: Modifier = Modifier,
    hourlyForecast: SimpleHourlyForecast,
    navigate: (NestedWeatherRoutes) -> Unit,
) {
    SimpleWeatherScreenBackground(modifier = modifier,
        cardInfo = CardInfo(title = stringResource(id = R.string.hourly_forecast),
            buttons = listOf(
                stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.comparison) to {
                    navigate(NestedWeatherRoutes.ComparisonHourlyForecast)
                },
                stringResource(id = R.string.detail) to {
                    navigate(NestedWeatherRoutes.DetailHourlyForecast)
                },
            ),
            content = {
                val lazyListState = rememberLazyListState()
                DynamicDateTime(hourlyForecast.dateTimeInfo, lazyListState)
                HourlyForecastItem(simpleHourlyForecast = hourlyForecast, lazyListState)
            }))
}