package io.github.pknujsp.everyweather.feature.weather.summary

import io.github.pknujsp.everyweather.core.data.ai.Prompt
import io.github.pknujsp.everyweather.core.model.airquality.AirQualityEntity
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider
import io.github.pknujsp.everyweather.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.everyweather.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.everyweather.core.model.weather.hourlyforecast.HourlyForecastEntity
import java.lang.ref.WeakReference
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class WeatherSummaryPrompt(
    private val model: Model
) : Prompt {

    override val id: Int get() = model.id

    private companion object {
        private const val TIME = "## When weather data was generated : "

        private val CONSTRUCTION = """
    Role(Designated Role):
    Serve as an expert who deeply analyzes extensive weather data and provides users with weather information in a concise, clear, and intuitive manner.

    Context(Situation and Goal):
    - The goal is to analyze and deliver current weather conditions, hourly and daily forecasts, and air quality information to users.
    - The analysis should focus on major trends and significant changes in the weather pattern, emphasizing changes in temperature, precipitation, wind speed, etc.
    - The weather data's specific location is not included; the template should guide on how to incorporate location-based analysis if needed.

    Input Values:
    - Current weather conditions
    - Hourly weather forecast data
    - Daily weather forecast data
    - Air quality information

    Instructions(Step-by-Step Guidance):
    1. Begin by analyzing the current weather conditions, including weather condition, temperature, feels-like temperature, humidity, wind speed, and direction. Summarize these in a way that's easy for users to grasp quickly.
    2. For the hourly forecast, highlight the major trends in weather conditions, temperature changes, precipitation probability and volume, humidity, and wind speed for the next 24 hours. Focus on significant changes or patterns.
    3. Analyze the daily forecast by summarizing the expected weather conditions for the coming days, including minimum and maximum temperatures, day and night weather conditions, and any significant weather changes.
    4. Provide an analysis of the current and forecasted air quality, indicating the overall status and any health advisories if applicable.
    5. Conclude with a comprehensive summary that encapsulates the key points from the current weather, hourly and daily forecasts, and air quality analysis. Make predictions or suggestions as appropriate.

    Guidelines(Guideline for Prompt):
    - The response should be crafted in English.
    - Ensure the information is clear, concise, and easy to understand, avoiding technical jargon where possible.
    - Use markdown to format the text for better readability, utilizing headings, bullet points, and paragraphs effectively.

    Output Indicator:
    - Output format: Markdown
    
    ## Current Weather
    {Answer}
    
    ## Hourly Forecast
    {Answer}
    
    ## Daily Forecast
    {Answer}
    
    ## Air Quality
    {Answer}
    
    ## Summary
    {Answer}
    
    Be sure to follow all instructions.
    """.trimIndent()
    }

    override fun build(): String = WeakReference(StringBuilder()).get()?.run {
        appendLine(model.currentWeather)
        appendLine(model.hourlyForecast)
        appendLine(model.dailyForecast)
        if (model.airQuality != null) {
            appendLine(model.airQuality)
        }
        appendLine(CONSTRUCTION)
        appendLine(TIME)
        appendLine(model.time)
        toString()
    } ?: ""

    class Model(
        coodinate: Pair<Double, Double>,
        time: ZonedDateTime,
        weatherProvider: WeatherProvider,
        val currentWeather: CurrentWeatherEntity,
        val hourlyForecast: HourlyForecastEntity,
        val dailyForecast: DailyForecastEntity,
        var airQuality: AirQualityEntity? = null,
    ) {
        val id: Int = coodinate.hashCode() + weatherProvider.key
        val time: String = time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
}