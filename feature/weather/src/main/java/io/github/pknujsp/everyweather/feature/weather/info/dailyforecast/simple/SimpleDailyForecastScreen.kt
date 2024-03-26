package io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.simple

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.res.stringResource
import io.github.pknujsp.everyweather.core.ui.weather.item.WeatherItemCard
import io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.model.SimpleDailyForecast
import io.github.pknujsp.everyweather.feature.weather.route.NestedRoutes

@Composable
fun SimpleDailyForecastScreen(
    dailyForecast: SimpleDailyForecast,
    navigate: (NestedRoutes) -> Unit,
) {
    val currentNavigate by rememberUpdatedState(newValue = navigate)
    WeatherItemCard(
        title = stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.daily_forecast),
        isSuccessful = { dailyForecast.items.isNotEmpty() },
        onClickToDetail = {
            currentNavigate(NestedRoutes.DETAIL_DAILY_FORECAST)
        },
        onClickToCompare = {
            currentNavigate(NestedRoutes.COMP_DAILY_FORECAST)
        },
        content = {
            SimpleDailyForecastItem(dailyForecast)
        },
    )
}