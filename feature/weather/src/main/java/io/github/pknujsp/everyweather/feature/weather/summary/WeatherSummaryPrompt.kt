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
    - For hourly and daily forecasts, **focus on the overall trend** in your analysis.
    
    Tone:
    - The response should be positive, interesting, and fun.
    - Do not use polite expressions and honorifics.
    - Use professional vocabulary and sentence structure to instill confidence in the user.
    
    Answer Quality:
    - The answer should not be ambiguous, controversial, or off-topic.
    - The answer must be concise and clearly deliver the main points.
    - Logic and reasoning must be rigorous and intellectual.
    - The weather data's specific location (place) is not included in the input.
    - Since actual weathercasters or weather experts will evaluate the quality of the answer, it must be well-written.
    - When evaluated by an actual expert, the score should be 100 out of 100.
    
    Answer Instructions:
    - Write the answer with a minimum of 50 characters, maximum of 200 characters per paragraph.
    - Divide the text into paragraphs for easy reading.
    - **Answer in korean**
    
    Answer Format:
    
    ### **Current**
    - Summarize the current weather in sentences only.
    
    ### **Hourly**
    - Analyze the hourly forecast in sentences, focusing on important times without listing every hour, to grasp the **overall trend**.
    - If the probability of precipitation is 30% or less, it is considered very low.
    
    ### **Daily**
    - Analyze the daily forecast in sentences, focusing on important times without listing every day, to grasp the **overall trend**.
    
    ### **Air Quality**
    - Describe the air quality in sentences.
    
    ### **Guidance**
    - Provide at least three practical pieces of advice to users based on a comprehensive analysis of the weather data.
    
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