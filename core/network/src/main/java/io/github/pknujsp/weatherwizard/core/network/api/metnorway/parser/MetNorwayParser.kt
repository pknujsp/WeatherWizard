package io.github.pknujsp.weatherwizard.core.network.api.metnorway.parser

import io.github.pknujsp.weatherwizard.core.common.util.WeatherUtil
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.MetNorwayResponse
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayCurrentWeatherResponse
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayDailyForecastResponse
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayHourlyForecastResponse


object MetNorwayParser {

    private val weatherUtil = WeatherUtil()

    private val symbolsMap = mapOf(
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

    fun MetNorwayResponse.toCurrentWeather(): MetNorwayCurrentWeatherResponse = MetNorwayCurrentWeatherResponse(this, symbolsMap)

    fun MetNorwayResponse.toHourlyForecast() = MetNorwayHourlyForecastResponse(this, weatherUtil, symbolsMap)

    fun MetNorwayResponse.toDailyForecast() = MetNorwayDailyForecastResponse(this, symbolsMap)

}