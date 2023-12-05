package io.github.pknujsp.weatherwizard.feature.weather.info.dailyforecast.simple

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.SimpleDailyForecast
import io.github.pknujsp.weatherwizard.core.ui.weather.item.CardInfo
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherScreenBackground
import io.github.pknujsp.weatherwizard.feature.weather.NestedWeatherRoutes
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoViewModel

@Composable
fun SimpleDailyForecastScreen(dailyForecast: SimpleDailyForecast, navigate: (NestedWeatherRoutes) -> Unit) {
    SimpleWeatherScreenBackground(CardInfo(title = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.daily_forecast),
        buttons = listOf(
            stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.comparison) to {
                navigate(NestedWeatherRoutes.ComparisonDailyForecast)
            },
            stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.detail) to {
                navigate(NestedWeatherRoutes.DetailDailyForecast)
            },
        ),
        content = {
            SimpleDailyForecastItem(dailyForecast)
        }))
}