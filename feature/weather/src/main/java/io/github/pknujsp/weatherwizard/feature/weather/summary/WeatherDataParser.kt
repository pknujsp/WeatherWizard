package io.github.pknujsp.weatherwizard.feature.weather.summary

import io.github.pknujsp.weatherwizard.core.model.airquality.AirQualityEntity
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import java.lang.ref.WeakReference

object WeatherDataParser {

    private const val NEW_LINE = "\n"

    private const val CONSTRUCTION = """
        ## 아래의 날씨 예보를 실용적인 관점에서 요약해주세요. 
        - 외출할 때 필요한 정보(예: 우산 필요 여부, 추위나 더위 대비)와 일상 생활에 영향을 줄 수 있는 요소들(예: 체감 온도, 강수 확률)에 중점을 두어 설명해주세요.
        - 친절한 말투로 정리해주세요.
        - 읽기 편하게 너무 길지 않게 설명하고, 강조, 목록등을 사용하여 같이 잘 구분되게 내용을 구성해주세요.
        - Markdown 문법을 사용하여 작성해주세요.
    """

    private const val LOCATION_INFO = "지역명 : "

    suspend fun parse(
        address: String,
        currentWeather: CurrentWeatherEntity,
        hourlyForecast: HourlyForecastEntity,
        dailyForecast: DailyForecastEntity,
        airQuality: AirQualityEntity? = null
    ): String = WeakReference(StringBuilder()).get()?.run {
        append(CONSTRUCTION)
        appendLine()
        append(LOCATION_INFO)
        append(address)
        appendLine()
        append(currentWeather)
        append(hourlyForecast)
        append(dailyForecast)
        if (airQuality != null) {
            append(airQuality)
        }
        toString()
    } ?: ""
}