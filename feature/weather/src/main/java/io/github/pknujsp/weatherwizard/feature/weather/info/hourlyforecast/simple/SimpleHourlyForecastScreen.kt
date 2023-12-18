package io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.simple

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.weatherwizard.core.model.onLoading
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.SimpleHourlyForecast
import io.github.pknujsp.weatherwizard.core.ui.DynamicDateTime
import io.github.pknujsp.weatherwizard.core.ui.weather.item.CardInfo
import io.github.pknujsp.weatherwizard.core.ui.weather.item.HourlyForecastItem
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherScreenBackground
import io.github.pknujsp.weatherwizard.feature.weather.NestedWeatherRoutes
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoViewModel


@Composable
fun HourlyForecastScreen(hourlyForecast: SimpleHourlyForecast, navigate: (NestedWeatherRoutes) -> Unit) {

    SimpleWeatherScreenBackground(CardInfo(title = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.hourly_forecast),
        buttons = listOf(
            stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.comparison) to {
                navigate(NestedWeatherRoutes.ComparisonHourlyForecast)
            },
            stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.detail) to {
                navigate(NestedWeatherRoutes.DetailHourlyForecast)
            },
        ),
        content = {
            val lazyListState = rememberLazyListState()

            DynamicDateTime(hourlyForecast.dateTimeInfo, lazyListState)
            HourlyForecastItem(simpleHourlyForecast = hourlyForecast, lazyListState)
        }))

}