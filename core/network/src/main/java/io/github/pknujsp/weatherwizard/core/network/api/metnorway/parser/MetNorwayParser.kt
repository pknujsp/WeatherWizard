package io.github.pknujsp.weatherwizard.core.network.api.metnorway.parser

import io.github.pknujsp.weatherwizard.core.common.util.WeatherUtil
import io.github.pknujsp.weatherwizard.core.model.weather.common.DateTimeValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.HumidityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PercentageUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PressureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.PressureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedValueType
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.MetNorwayResponse
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayCurrentWeatherResponse
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayHourlyForecastResponse
import java.time.ZoneId
import java.time.ZonedDateTime


class MetNorwayParser {

    private val weatherUtil = WeatherUtil()

    private companion object {
        val symbolsMap = mapOf(
            "clearsky" to WeatherConditionCategory.Clear,
            "cloudy" to WeatherConditionCategory.Overcast,
            "fair" to WeatherConditionCategory.PartlyCloudy,
            "fog" to WeatherConditionCategory.Overcast,
            "heavyrain" to WeatherConditionCategory.Rain,
            "heavyrainandthunder" to WeatherConditionCategory.Rain,
            "heavyrainshowers" to WeatherConditionCategory.Rain,
            "heavyrainshowersandthunder" to WeatherConditionCategory.Rain,
            "heavysleet" to WeatherConditionCategory.RainAndSnow,
            "heavysleetandthunder" to WeatherConditionCategory.RainAndSnow,
            "heavysleetshowers" to WeatherConditionCategory.RainAndSnow,
            "heavysleetshowersandthunder" to WeatherConditionCategory.RainAndSnow,
            "heavysnow" to WeatherConditionCategory.Snow,
            "heavysnowandthunder" to WeatherConditionCategory.Snow,
            "heavysnowshowers" to WeatherConditionCategory.Snow,
            "heavysnowshowersandthunder" to WeatherConditionCategory.Snow,
            "lightrain" to WeatherConditionCategory.Rain,
            "lightrainandthunder" to WeatherConditionCategory.Rain,
            "lightrainshowers" to WeatherConditionCategory.Rain,
            "lightrainshowersandthunder" to WeatherConditionCategory.Rain,
            "lightsleet" to WeatherConditionCategory.RainAndSnow,
            "lightsleetandthunder" to WeatherConditionCategory.RainAndSnow,
            "lightsleetshowers" to WeatherConditionCategory.RainAndSnow,
            "lightsnow" to WeatherConditionCategory.Snow,
            "lightsnowandthunder" to WeatherConditionCategory.Snow,
            "lightsnowshowers" to WeatherConditionCategory.Snow,
            "lightssleetshowersandthunder" to WeatherConditionCategory.RainAndSnow,
            "lightssnowshowersandthunder" to WeatherConditionCategory.Snow,
            "partlycloudy" to WeatherConditionCategory.PartlyCloudy,
            "rain" to WeatherConditionCategory.Rain,
            "rainandthunder" to WeatherConditionCategory.Rain,
            "rainshowers" to WeatherConditionCategory.Rain,
            "rainshowersandthunder" to WeatherConditionCategory.Rain,
            "sleet" to WeatherConditionCategory.RainAndSnow,
            "sleetandthunder" to WeatherConditionCategory.RainAndSnow,
            "sleetshowers" to WeatherConditionCategory.RainAndSnow,
            "sleetshowersandthunder" to WeatherConditionCategory.RainAndSnow,
            "snow" to WeatherConditionCategory.Snow,
            "snowandthunder" to WeatherConditionCategory.Snow,
            "snowshowers" to WeatherConditionCategory.Snow,
            "snowshowersandthunder" to WeatherConditionCategory.Snow,
        )
    }

    fun MetNorwayResponse.toCurrentWeather(): MetNorwayCurrentWeatherResponse =
        MetNorwayCurrentWeatherResponse(dateTime = DateTimeValueType(ZonedDateTime.parse(properties.timeseries[0].time)
            .withZoneSameInstant(ZoneId
                .systemDefault()).toString()),
            temperature = TemperatureValueType(properties.timeseries[0].data.instant.details.airTemperature, TemperatureUnit.Celsius),
            feelsLikeTemperature = TemperatureValueType(properties.timeseries[0].data.instant.details.airTemperature,
                TemperatureUnit.Celsius),
            humidity = HumidityValueType(properties.timeseries[0].data.instant.details.relativeHumidity.toInt(), PercentageUnit),
            windDirection = WindDirectionValueType(properties.timeseries[0].data.instant.details.windFromDirection.toInt(),
                WindDirectionUnit.Degree),
            windSpeed = WindSpeedValueType(properties.timeseries[0].data.instant.details.windSpeed, WindSpeedUnit.MeterPerSecond),
            precipitationVolume = PrecipitationValueType(properties.timeseries[0].data.next1Hours!!.details.precipitationAmount,
                PrecipitationUnit.Millimeter),
            weatherCondition = WeatherConditionValueType(symbolsMap[properties.timeseries[0].data.next1Hours!!.summary.symbolCode]!!),
            dewPointTemperature = TemperatureValueType(properties.timeseries[0].data.instant.details.dewPointTemperature,
                TemperatureUnit.Celsius),
            airPressure = PressureValueType(properties.timeseries[0].data.instant.details.airPressureAtSeaLevel.toInt(), PressureUnit
                .Hectopascal)
        )

    fun MetNorwayResponse.toHourlyForecast() = MetNorwayHourlyForecastResponse(this, weatherUtil, symbolsMap)

}