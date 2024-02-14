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
    - The response should be positive, interesting, and fun.
    - Do not use polite expressions and honorifics.
    - Use professional vocabulary and sentence structure to instill confidence in the user.
    
    Answer Quality:
    - The answer should not be ambiguous, controversial, or off-topic.
    - Information needs to be clear and easy to understand
    - The answer must be concise and clearly deliver the main points.
    - Logic and reasoning must be rigorous and intellectual.
    - Since actual weathercasters or weather experts will evaluate the quality of the answer, it must be well-written.
    - When evaluated by an actual expert, the score should be 99 out of 100.
    
    Answer Instructions:
    - **Answer in korean**
    - Write the answer with a minimum of 60 characters.
    - Divide the text into paragraphs for easy reading.
    
    Answer Format:
    
    ### **Current**
    - Summarize the current weather in sentences.
    
    Example (For reference only, do not copy!): {
    The weather is currently mostly cloudy, with a temperature of 12°C, which is the same as the air temperature. The humidity is very high at 100%, the wind is light at 0.3 m/s, and the wind direction is 135°. No precipitation information was provided.
    }
    
    ### **Hourly**
    - Analyze the hourly forecast in sentences.
    - If the probability of precipitation is 30% or less, it is considered very low.
    - Goal: Provide information to help viewers quickly understand key weather changes over time.
    - Analyze data: Analyze key weather elements such as temperature, precipitation probability, wind speed, and cloud cover by time of day.
    - Highlight key times of day: Highlight the times of day when weather changes the most (e.g., rush hour, lunch, and dinnertime), detailing temperature changes, precipitation probability, and unusual events during those times.
    
    Example (For reference only, do not copy!): {
    The weather is mostly cloudy from morning to evening today. Temperatures will gradually rise from 11°C to 15°C, peaking at 15°C around 3pm.
    The chance of precipitation is mostly low, but there is a 60% chance of rain at 11am and between 4pm and 7pm, especially at 6pm when it could be a little heavier, with 3.0mm of precipitation expected.
    Winds will gradually increase, reaching up to 4.0 m/s in the afternoon, requiring caution when outdoors. 
    }
    
    ### **Daily**
    - Analyze the daily forecast in sentences.
    - Goal: Clearly and concisely communicate key weather patterns and changes for the week.
    - Capture key changes: Analyze daily highs and lows, precipitation, wind strength and direction, and the likelihood of special weather events (e.g., heavy rain, storms, snow).
    - Presenting weekly highlights: Highlight significant changes or patterns in weekly weather. For example, detail an increase in temperature mid-week, the likelihood of precipitation over the weekend, etc.
    - Mention the reliability of the forecast: For long-term forecasts, mention the reliability or uncertainty of the forecast so that viewers can take this into account.
    
    Example (For reference only, do not copy!): {
    The temperature change over the next few days will be significant, especially from February 17 to 19, with temperatures gradually rising, reaching a high of 17°C on the 19th.
    Rain is in the forecast from February 19, and will continue on the 20th and 21st, which may affect your plans for outdoor activities.
    }
    
    ### **Air Quality**
    - Describe the air quality in sentences.
    
    Example (For reference only, do not copy!): {
    Currently, air quality is 'moderate' and is expected to remain mostly 'good' for the next week before changing to 'moderate' on February 20th.
    }
    
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