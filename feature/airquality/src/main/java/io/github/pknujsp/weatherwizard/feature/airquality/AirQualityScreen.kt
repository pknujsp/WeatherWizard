package io.github.pknujsp.weatherwizard.feature.airquality

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.pknujsp.weatherwizard.core.model.airquality.SimpleAirQuality
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.weatherwizard.core.model.weather.common.AirQualityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataCategory
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.weather.item.CardInfo
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherFailedBox
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherScreenBackground
import java.time.ZonedDateTime


@Composable
fun AirQualityScreen(
    requestWeatherArguments: RequestWeatherArguments,
    dateTime:ZonedDateTime,
    onAirQualityLoaded: (AirQualityValueType) -> Unit,
    viewModel: AirQualityViewModel = hiltViewModel()
) {
    val airQuality = viewModel.airQuality
    val airQualityCallback by rememberUpdatedState(onAirQualityLoaded)

    LaunchedEffect(requestWeatherArguments) {
        viewModel.loadAirQuality(requestWeatherArguments.latitude, requestWeatherArguments.longitude)
    }

    if (!airQuality.isLoading) {
        airQuality.airQuality?.let {
            airQualityCallback(it.current.aqi)
            SimpleWeatherScreenBackground(cardInfo = CardInfo(title = stringResource(io.github.pknujsp.weatherwizard.core.resource.R.string.air_quality_index),
                content = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        SimpleCurrentContent(simpleAirQuality = it)
                        BarGraph(forecast = it.dailyForecast, dateTime.toLocalDate())
                    }
                }))
        } ?: run {
            SimpleWeatherFailedBox(title = stringResource(id = R.string.air_quality_index),
                description = stringResource(id = airQuality.failedReason!!.message)) {
                viewModel.reload()
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SimpleCurrentContent(simpleAirQuality: SimpleAirQuality) {
    FlowItem(pollutantStringResId = WeatherDataCategory.AIR_QUALITY_INDEX.stringId, value = simpleAirQuality.current.aqi)
    FlowRow(maxItemsInEachRow = 3,
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()) {
        simpleAirQuality.grids.forEach { (pollutantStringResId, value) ->
            FlowItem(pollutantStringResId = pollutantStringResId, value = value)
        }
    }
}

@Composable
private fun FlowItem(modifier: Modifier = Modifier, pollutantStringResId: Int, value: AirQualityValueType) {
    Column(
        modifier = modifier.padding(vertical = 6.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = stringResource(id = pollutantStringResId),
            fontSize = 13.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
        ) {
            Box(modifier = Modifier
                .size(14.dp)
                .background(value.airQualityDescription.color, CircleShape))
            Text(
                text = stringResource(value.airQualityDescription.descriptionStringId),
                fontSize = 14.sp,
                color = Color.White,
            )
        }
    }
}