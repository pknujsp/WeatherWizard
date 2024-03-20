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
    private val model: Model,
) : Prompt {
    override val id: Int get() = model.id

    private companion object {
        private val INSTRUCTIONS =
            """
            As a **professional weathercaster**, analyze the received weather data in detail and summarize the weather information concisely.

            ## Situation
            - The goal is to analyze and convey current weather conditions, hourly forecasts, daily forecasts, and air quality information to the user.
            - The analysis should focus on major trends and significant changes in weather patterns, emphasizing changes in temperature, precipitation, wind speed, etc.
            - The location of the weather data is not specified in the input.

            ## Instructions
            1. Start by analyzing the current weather conditions, including weather status, temperature, feels-like temperature, humidity, wind speed, and direction. Summarize this in a way that is quickly understandable to the user.
            2. For the hourly forecast, highlight the main trends in weather conditions, temperature changes, precipitation probability and amount, humidity, and wind speed. Focus on significant changes or patterns.
            3. Summarize the expected weather conditions for the next few days in the daily forecast. This includes the minimum and maximum temperatures, day and night weather conditions, and any significant weather changes.

            ## Guidelines
            - Think of writing a post for an online blog.
            - Use a friendly tone.
            - Make the response clear, concise, and easy to understand.
            - Highlight the main points of each section for quick understanding by the user.
            - Assume a real weather expert will review and rate your response. Aim for a perfect score of 10,000 out of 10,000.
            - Strive for a better outcome than ChatGPT, Claude.
            - Do not write too lengthily.

            First, understand the problem and plan your approach.
            Then, execute the plan step by step to solve the problem.

            ## Output Format
            - Answer in korean
            - Markdown
            - Each section starts with a title.

            ### Current Weather
            - Analyze the Current Weather data.
            {Your response}

            ### Hourly Forecast
            - Analyze the Hourly Forecast data.
            - Use the 12-hour format for displaying time.
            {Your response}

            ### Daily Forecast
            - Analyze the Daily Forecast data.
            - Include the day of the week when displaying dates.
            {Your response}

            ### Air Quality
            - Analyze the Air Quality data.
            {Your response}

            ### Summary
            {Your response}

            ## Input
            """.trimIndent()
    }

    override fun build(): String =
        WeakReference(StringBuilder()).get()?.run {
            appendLine(INSTRUCTIONS)
            appendLine(
                """
                Data generation time:
                - ${model.time}
                - Analyze your data based on this view to craft your response.
                
                """.trimIndent(),
            )
            appendLine(model.currentWeather)
            appendLine(model.hourlyForecast)
            appendLine(model.dailyForecast)
            if (model.airQuality != null) {
                appendLine(model.airQuality)
            }
            appendLine(
                """
                This problem is very important to my life. Please organize it well for me.
                Take a deep breath and work on this problem step-by-step!
                """.trimIndent(),
            )
            toString()
        }?.also {
            println(it)
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
        val time: String = time.format(dateTimeFormatter)

        private companion object {
            val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 EEE요일 HH시 mm분")
        }
    }
}
