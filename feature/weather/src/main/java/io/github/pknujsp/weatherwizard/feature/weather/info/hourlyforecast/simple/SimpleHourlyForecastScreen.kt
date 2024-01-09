package io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.simple

import android.content.res.Resources
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
    onCalculatedY: (Dp) -> Unit
) {
    val isCalculatedY = remember(hourlyForecast) { BooleanArray(1) { false } }
    val calculatedY by rememberUpdatedState(onCalculatedY)
    val density = LocalDensity.current
    val systemBars = androidx.compose.foundation.layout.WindowInsets.systemBars
    val localConfiguration = LocalConfiguration.current

    val screenHeight = remember {
        (localConfiguration.screenHeightDp * density.density) + systemBars.getBottom(density) + systemBars.getTop(density)
    }

    SimpleWeatherScreenBackground(modifier = modifier.onPlaced { coordinates ->
        if (!isCalculatedY[0]) {
            isCalculatedY[0] = true
            val positionInWindow = coordinates.positionInWindow()
            val bottom = positionInWindow.y + coordinates.size.height
            calculatedY(((screenHeight - bottom) / density.density).dp)
        }
    },
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