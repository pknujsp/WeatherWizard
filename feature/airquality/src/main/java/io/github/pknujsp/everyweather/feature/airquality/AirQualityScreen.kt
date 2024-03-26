package io.github.pknujsp.everyweather.feature.airquality

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
import io.github.pknujsp.everyweather.core.model.airquality.AirQualityEntity
import io.github.pknujsp.everyweather.core.model.airquality.SimpleAirQuality
import io.github.pknujsp.everyweather.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.everyweather.core.model.weather.common.AirQualityValueType
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.weather.item.WeatherItemCard
import java.time.ZonedDateTime

@Composable
fun AirQualityScreen(
    requestWeatherArguments: RequestWeatherArguments,
    dateTime: ZonedDateTime,
    onLoadAirQuality: (AirQualityEntity) -> Unit,
    viewModel: AirQualityViewModel = hiltViewModel(),
) {
    val airQuality = viewModel.airQuality
    val currentLoadAirQuality by rememberUpdatedState(onLoadAirQuality)

    LaunchedEffect(requestWeatherArguments) {
        viewModel.loadAirQuality(requestWeatherArguments.targetLocation.latitude, requestWeatherArguments.targetLocation.longitude)
    }

    LaunchedEffect(requestWeatherArguments, airQuality.airQuality) {
        if (airQuality.entity != null) {
            currentLoadAirQuality(airQuality.entity!!)
        }
    }

    if (!airQuality.isLoading) {
        WeatherItemCard(title = stringResource(id = R.string.air_quality),
            isSuccessful = { airQuality.airQuality != null },
            onClickToRefresh = { viewModel.reload() },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, bottom = 8.dp),
                ) {
                    SimpleCurrentContent(simpleAirQuality = airQuality.airQuality!!)
                    BarGraph(forecast = airQuality.airQuality!!.dailyForecast, dateTime.toLocalDate())
                }
            })
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SimpleCurrentContent(simpleAirQuality: SimpleAirQuality) {
    FlowItem(pollutantStringResId = R.string.current_air_quality, value = simpleAirQuality.current.aqi)
    FlowRow(
        maxItemsInEachRow = 4,
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth(),
    ) {
        simpleAirQuality.grids.forEach { (pollutantStringResId, value) ->
            FlowItem(pollutantStringResId = pollutantStringResId, value = value)
        }
    }
}

@Composable
private fun FlowItem(
    modifier: Modifier = Modifier,
    pollutantStringResId: Int,
    value: AirQualityValueType,
) {
    Column(
        modifier = modifier.padding(vertical = 6.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = stringResource(id = pollutantStringResId),
            fontSize = 14.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
        ) {
            Box(
                modifier = Modifier
                    .size(15.dp)
                    .background(value.airQualityDescription.color, CircleShape),
            )
            Text(
                text = stringResource(value.airQualityDescription.descriptionStringId),
                fontSize = 15.sp,
                color = Color.White,
            )
        }
    }
}