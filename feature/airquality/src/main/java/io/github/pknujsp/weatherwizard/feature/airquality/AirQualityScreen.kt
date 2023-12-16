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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.model.airquality.AirPollutants
import io.github.pknujsp.weatherwizard.core.model.airquality.SimpleAirQuality
import io.github.pknujsp.weatherwizard.core.model.onError
import io.github.pknujsp.weatherwizard.core.model.onLoading
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.weatherwizard.core.ui.theme.AppShapes
import io.github.pknujsp.weatherwizard.core.ui.weather.item.CardInfo
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherFailedBox
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherScreenBackground


@Composable
fun AirQualityScreen(requestWeatherArguments: RequestWeatherArguments, viewModel: AirQualityViewModel = hiltViewModel()) {
    val airQuality by viewModel.airQuality.collectAsStateWithLifecycle()

    airQuality.onLoading {
        requestWeatherArguments.run {
            viewModel.loadAirQuality(requestWeatherArguments.location.latitude, requestWeatherArguments.location.longitude)
        }
    }.onSuccess {
        SimpleWeatherScreenBackground(CardInfo(title = stringResource(io.github.pknujsp.weatherwizard.core.resource.R.string.air_quality_index),
            content = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    SimpleCurrentContent(simpleAirQuality = it)
                    BarGraph(forecast = it.dailyForecast)
                }
            }))
    }.onError {
        SimpleWeatherFailedBox(title = stringResource(id = R.string.air_quality_index),
            description = stringResource(id = R.string.data_downloaded_failed)) {
            viewModel.reload()
        }
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SimpleCurrentContent(simpleAirQuality: SimpleAirQuality) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${stringResource(R.string.current_air_quality)} ", fontSize = 13.sp,
            color = Color.White,
        )
        Box(
            modifier = Modifier
                .background(simpleAirQuality.current.aqi.airQualityDescription.color, AppShapes.medium)
                .padding(6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(simpleAirQuality.current.aqi.airQualityDescription.descriptionStringId),
                fontSize = 15.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
            )
        }
    }

    val grids = simpleAirQuality.current.run {
        listOf(
            stringResource(AirPollutants.PM10.nameResId) to pm10,
            stringResource(AirPollutants.PM25.nameResId) to pm25,
            stringResource(AirPollutants.O3.nameResId) to o3,
            stringResource(AirPollutants.NO2.nameResId) to no2,
            stringResource(AirPollutants.SO2.nameResId) to so2,
            stringResource(AirPollutants.CO.nameResId) to co,
        )
    }
    FlowRow(maxItemsInEachRow = 3,
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()) {
        grids.forEach { (pollutant, value) ->

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(6.dp, 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = pollutant,
                    fontSize = 13.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
                Box(
                    modifier = Modifier
                        .background(value.airQualityDescription.color, AppShapes.medium)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(value.airQualityDescription.descriptionStringId),
                        fontSize = 15.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center,
                    )
                }
            }


        }
    }
}