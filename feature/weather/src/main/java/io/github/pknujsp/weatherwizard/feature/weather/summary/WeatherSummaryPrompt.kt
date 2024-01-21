package io.github.pknujsp.weatherwizard.feature.weather.summary

import io.github.pknujsp.weatherwizard.core.data.ai.Prompt
import io.github.pknujsp.weatherwizard.core.model.airquality.AirQualityEntity
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import java.lang.ref.WeakReference

class WeatherSummaryPrompt(
    private val model: Model
) : Prompt {

    override val id: Int get() = model.id

    private companion object {
        private const val TIME = "## 날씨 데이터 생성 날짜 및 시각(ISO-8601) : "

        private val CONSTRUCTION = """
    # 아래의 날씨 예보를 실용적인 관점에서 요약해주세요.
    
    - 친절한 말투로 정리해주세요.
    - Markdown 문법을 사용하여 작성해주세요.
    - 데이터의 형식이나 값이 변경되어도 아래의 분석 방법을 적용하여 항상 일관된 형식의 답변을 하세요.
    - 현재 날씨, 시간별 예보, 일별 예보, 대기질 데이터를 모두 활용하여 답변을 하세요.
    - NaNmm 또는 NaN% 등의 값이 있을 경우, 데이터가 없는 것이므로 이와 관련된 내용은 다루지 마세요.
    
    ## 분석 방법
    
    1. 제공된 날씨 데이터를 바탕으로, 사람들에게 제공할 수 있는 세 가지 구체적이고 실질적인 날씨 관련 조언을 제공하세요.
    2. 외출할 때 필요한 정보(예: 우산 필요 여부, 추위나 더위 대비)와 일상 생활에 영향을 줄 수 있는 요소들(예: 체감 온도, 강수 확률)에 중점을 두어 설명해주세요.
    
    ## 답변 형식
    
    ### 한 줄 요약
    {내용}
    
    ### 현재 날씨 요약
    {내용}
    
    ### 시간별 예보 요약
    {내용}
    
    ### 일별 예보 요약
    {내용}
    
    ### 대기질 요약
    {내용}
    
    ### 조언
    {내용}
    """.trimIndent()
    }

    override fun build(): String = WeakReference(StringBuilder()).get()?.run {
        append(CONSTRUCTION)
        appendLine()
        append(TIME)
        append(model.time)
        appendLine()
        append(model.currentWeather)
        appendLine()
        append(model.hourlyForecast)
        appendLine()
        append(model.dailyForecast)
        appendLine()
        if (model.airQuality != null) {
            append(model.airQuality)
        }
        toString()
    } ?: ""

    class Model(
        val coodinate: Pair<Double, Double>,
        val time: String,
        val weatherProvider: WeatherProvider,
        val currentWeather: CurrentWeatherEntity,
        val hourlyForecast: HourlyForecastEntity,
        val dailyForecast: DailyForecastEntity,
        var airQuality: AirQualityEntity? = null,
    ) {
        val id: Int = coodinate.hashCode() + weatherProvider.key
    }
}