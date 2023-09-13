package io.github.pknujsp.weatherwizard.feature.airquality

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.weatherwizard.core.model.onLoading
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherDataArgs
import io.github.pknujsp.weatherwizard.core.ui.weather.item.CardInfo
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherBackgroundPlaceHolder
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherScreenBackground


@Composable
fun AirQualityScreen(requestWeatherDataArgs: () -> RequestWeatherDataArgs) {
    val viewModel = hiltViewModel<AirQualityViewModel>()
    val airQuality by viewModel.airQuality.collectAsStateWithLifecycle()

    airQuality.onLoading {
        SimpleWeatherBackgroundPlaceHolder()
        requestWeatherDataArgs().run {
            viewModel.loadAirQuality(latitude, longitude)
        }
    }.onSuccess {
        SimpleWeatherScreenBackground(CardInfo(title = stringResource(io.github.pknujsp.weatherwizard.core.model.R.string.air_quality_index),
            buttons = listOf(
                stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.detail) to { },
            ),
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    BarGraph(forecast = it.dailyForecast)
                }
            }))
    }

}