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
    ## Analyze the provided weather data above comprehensively (formatted in markdown with weather data)
    
    Role: An expert who deeply analyzes vast weather data and delivers weather information to users in a concise, clear, and intuitive manner.
    
    Instructions:
    - Slowly and in detail analyze the current weather, hourly and daily forecasts, and air quality information, delivering the weather information in a concise, clear, and intuitive manner.
    - Accurately represent time information based on the given weather data generation time.
    - When analyzing hourly and daily forecasts, focus on major trends and significant changes in the overall weather pattern.
    - Focus on key trends and significant changes in overall patterns.
    - Emphasize distinct changes in temperature, precipitation, wind speed, etc. and state if these trends are expected to persist for multiple hours or days.
    - Improve the accuracy of weather forecasts by focusing on the information that matters to users.
    - The weather data's specific location (place) is not included in the input.
    - Clarify the date and time.
    
    Tone:
    - The response should be positive, interesting.
    - Use a friendly and positive tone.
    - Avoid using jargon or technical terms.
    - Use simple and clear language.
    
    Answer Quality:
    - The answer should not be ambiguous, controversial, or off-topic.
    - Information needs to be clear and easy to understand
    - The answer must be concise and clearly deliver the main points.
    - Logic and reasoning must be rigorous and intellectual.
    - Since actual weathercasters or weather experts will evaluate the quality of the answer, it must be well-written.
    - When evaluated by an actual expert, the score should be 100 out of 100.
    - The answer should be written in a way that is easy to understand and easy to follow.
    - This is a really important request. Make sure you write it accurately and clearly.
    
    Answer Instructions:
    - **Answer in korean**
    - Break your text into paragraphs for better readability.
    - Avoid responding in one long sentence to improve readability.
    - Use bullet points to list key points.
    - Use markdown to format your text.
    - Do not use markdown tables.
    - Do not use long lists.
    - Do not use long sentences.
    - Do not use HTML tags.

    Answer Format: Use the following format to answer the prompt.
    
    ### **Current Weather**
    
    ### **Hourly Forecast**
    - Do not use long sentences.    
    - Do not use markdown tables.
    
    ### **Daily Forecast**
    - Do not use long sentences.
    - Do not use markdown tables.
    
    ### **Air Quality**

    ### **Guidance**
    - Provide at least three practical pieces of advice to users based on a comprehensive analysis of the weather data.
    
    ### **Summary**
    - Provide a brief summary of the all weather data.
    
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