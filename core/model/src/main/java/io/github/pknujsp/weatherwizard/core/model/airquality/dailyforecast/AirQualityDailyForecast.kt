package io.github.pknujsp.weatherwizard.core.model.airquality.dailyforecast

import androidx.compose.runtime.Stable
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.airquality.AirPollutants
import io.github.pknujsp.weatherwizard.core.model.airquality.AirQualityDescription
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Stable
data class AirQualityDailyForecast(
    val items: List<Item>,
) : UiModel {
    val simpleItems: List<SimpleDailyItem> = items.run {
        val maxes = map { item ->
            item to item.pollutants.maxBy { it.index }
        }
        val maxIndex = maxes.maxOf { it.second.index }
        val minIndex = maxes.minOf { it.second.index }
        val indexRange = (maxIndex - minIndex).toFloat()

        val dateTimeFormatter = DateTimeFormatter.ofPattern("M/d\nE")
        maxes.map {
            SimpleDailyItem(
                dateTime = it.first.dateTime.format(dateTimeFormatter),
                airQualityDescription = it.second.airQualityDescription,
                index = it.second.index,
                barHeightRatio = ((it.second.index - minIndex) / indexRange).coerceAtLeast(0.2f).coerceAtMost(1f)
            )
        }
    }

    @Stable
    data class Item(
        val dateTime: ZonedDateTime,
        val pollutants: List<Pollutant>,
    ) {

        @Stable
        data class Pollutant(
            val airPollutants: AirPollutants,
            val airQualityDescription: AirQualityDescription,
            val index: Int,
        )
    }

    @Stable
    data class SimpleDailyItem(
        val dateTime: String,
        val airQualityDescription: AirQualityDescription,
        val index: Int,
        val barHeightRatio: Float
    )

}


// dummy

val dummyAirQualityDailyForecast = AirQualityDailyForecast(
    items = listOf(
        AirQualityDailyForecast.Item(
            dateTime = ZonedDateTime.now(),
            pollutants = listOf(
                AirQualityDailyForecast.Item.Pollutant(
                    airPollutants = AirPollutants.PM10,
                    airQualityDescription = AirQualityDescription.GOOD,
                    index = 10,
                ),
                AirQualityDailyForecast.Item.Pollutant(
                    airPollutants = AirPollutants.PM25,
                    airQualityDescription = AirQualityDescription.GOOD,
                    index = 38,
                ),
                AirQualityDailyForecast.Item.Pollutant(
                    airPollutants = AirPollutants.O3,
                    airQualityDescription = AirQualityDescription.GOOD,
                    index = 53,
                ),
                AirQualityDailyForecast.Item.Pollutant(
                    airPollutants = AirPollutants.NO2,
                    airQualityDescription = AirQualityDescription.GOOD,
                    index = 2546,
                ),
                AirQualityDailyForecast.Item.Pollutant(
                    airPollutants = AirPollutants.CO,
                    airQualityDescription = AirQualityDescription.GOOD,
                    index = 24,
                ),
                AirQualityDailyForecast.Item.Pollutant(
                    airPollutants = AirPollutants.SO2,
                    airQualityDescription = AirQualityDescription.GOOD,
                    index = 10,
                ),
            )
        ),
        AirQualityDailyForecast.Item(
            dateTime = ZonedDateTime.now().plusDays(1),
            pollutants = listOf(
                AirQualityDailyForecast.Item.Pollutant(
                    airPollutants = AirPollutants.PM10,
                    airQualityDescription = AirQualityDescription.UNHEALTHY,
                    index = 23,
                ),
                AirQualityDailyForecast.Item.Pollutant(
                    airPollutants = AirPollutants.PM25,
                    airQualityDescription = AirQualityDescription.GOOD,
                    index = 12,
                ),
                AirQualityDailyForecast.Item.Pollutant(
                    airPollutants = AirPollutants.O3,
                    airQualityDescription = AirQualityDescription.GOOD,
                    index = 2,
                ),
                AirQualityDailyForecast.Item.Pollutant(
                    airPollutants = AirPollutants.NO2,
                    airQualityDescription = AirQualityDescription.GOOD,
                    index = 3,
                ),
            )
        ))
)