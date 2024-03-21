package io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.simple

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.res.stringResource
import io.github.pknujsp.everyweather.core.ui.weather.item.CardInfo
import io.github.pknujsp.everyweather.core.ui.weather.item.SimpleWeatherScreenBackground
import io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.model.SimpleDailyForecast
import io.github.pknujsp.everyweather.feature.weather.route.NestedRoutes

@Composable
fun SimpleDailyForecastScreen(
    dailyForecast: SimpleDailyForecast,
    navigate: (NestedRoutes) -> Unit,
) {
    val currentNavigate by rememberUpdatedState(newValue = navigate)
    SimpleWeatherScreenBackground(
        cardInfo = CardInfo(
            title = stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.daily_forecast),
            buttons = listOf(
                stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.comparison) to {
                    currentNavigate(NestedRoutes.COMP_DAILY_FORECAST)
                },
                stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.detail) to {
                    currentNavigate(NestedRoutes.DETAIL_DAILY_FORECAST)
                },
            ),
            content = {
                SimpleDailyForecastItem(dailyForecast)
            },
        ),
    )
}