package io.github.pknujsp.weatherwizard.feature.airquality

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.weatherwizard.core.common.util.AStyle
import io.github.pknujsp.weatherwizard.core.common.util.toAnnotated
import io.github.pknujsp.weatherwizard.core.model.R
import io.github.pknujsp.weatherwizard.core.model.airquality.AirPollutants
import io.github.pknujsp.weatherwizard.core.model.airquality.SimpleAirQuality
import io.github.pknujsp.weatherwizard.core.model.onError
import io.github.pknujsp.weatherwizard.core.model.onLoading
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherDataArgs
import io.github.pknujsp.weatherwizard.core.ui.weather.item.CardInfo
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherBackgroundPlaceHolder
import io.github.pknujsp.weatherwizard.core.ui.weather.item.SimpleWeatherFailedBox
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
            content = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    SimpleCurrent(simpleAirQuality = it)
                    BarGraph(forecast = it.dailyForecast)
                }
            }))
    }.onError {
        SimpleWeatherFailedBox(title = stringResource(id = R.string.air_quality_index),
            description = stringResource(id = io.github.pknujsp.weatherwizard.feature.airquality.R.string.data_downloaded_failed)) {
            viewModel.reload()
        }
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColumnScope.SimpleCurrent(simpleAirQuality: SimpleAirQuality) {
    Text(text = listOf(AStyle(text = "${stringResource(io.github.pknujsp.weatherwizard.feature.airquality.R.string.current_air_quality)}: ",
        span = SpanStyle(color = Color.White, fontSize = 13.sp)),
        AStyle(text = stringResource(simpleAirQuality.current.aqi.airQualityDescription.descriptionStringId),
            span = SpanStyle(color = simpleAirQuality.current.aqi.airQualityDescription.color, fontSize = 16.sp)),
        AStyle(text = "(${simpleAirQuality.current.aqi})",
            span = SpanStyle(color = simpleAirQuality.current.aqi.airQualityDescription.color,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold))).toAnnotated())

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


    FlowRow(maxItemsInEachRow = 3, verticalArrangement = Arrangement.Center, horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()) {
        grids.forEach { (pollutant, value) ->
            Text(
                text = listOf(AStyle(text = "${pollutant}\n", span = SpanStyle(color = Color.White, fontSize = 13.sp)),
                    AStyle(text = stringResource(value.airQualityDescription.descriptionStringId),
                        span = SpanStyle(color = value.airQualityDescription.color,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold))).toAnnotated(),
                modifier = Modifier
                    .weight(1f)
                    .padding(6.dp, 4.dp),
                lineHeight = 24.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}