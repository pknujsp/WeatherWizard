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
        private const val TIME = "## 날씨 데이터 생성 시점 : "

        private val CONSTRUCTION = """
    ## 위에 제공된 날씨 데이터를 종합적으로 분석하세요(마크다운 형식으로 날씨 데이터가 구성되어 있음)        
            
    역할: 방대한 날씨 데이터를 심도깊이 분석하여, 사용자에게 날씨 정보를 간결하고 명확하게 전달하는 전문가
    
    지시사항:
    - 현재 날씨와 시간별, 일별 예보, 대기질 정보를 천천히 상세하게 분석하여, 날씨 정보를 간결하고 명확하게 직관적으로 전달합니다.
    - 주어진 날씨 데이터 생성 시점을 바탕으로 시간 정보를 올바르게 나타내도록 합니다.
    - 시간별 예보, 일별 예보의 경우 **전체적인 추세에 집중**하여 분석하세요.
    
    어조:
    - 답변은 긍정적이고, 흥미롭고, 재미있어야 합니다.
    - 공손한 표현과 높임말을 사용하지 마세요.
    - 전문적인 어휘와 문장 구조를 사용하여, 사용자에게 신뢰감을 줍니다.
    
    답변 품질:
    - 답변은 모호하거나 논란의 여지가 있거나 주제를 벗어나지 않아야 합니다.
    - 답변은 간결하고 요점을 명확하게 전달해야 합니다.
    - 논리와 추론은 엄격하고 지적인 것이어야 합니다.
    - 날씨 데이터가 어느 지역(장소)에 대한 것인지는 입력에 포함되어 있지 않습니다.
    - 실제 기상 캐스터, 날씨 전문가가 답변 품질을 평가하므로 잘 작성해야 합니다.
    - 실제 전문가가 평가하였을 때 평가 점수가 100점 만점에 100점이 되도록 해야 합니다.
    
    답변 지시사항: 
    - 답변은 단락 별로 최소 50자 이상으로 작성합니다.
    - 읽기 쉽도록 단락을 나누세요.
    
    답변 형식:
    
    ### **현재**
    - 현재 날씨를 요약하여, 문장으로만 설명.
    
    ### **시간별**
    - 시간별 예보를 분석하여, 문장으로만 설명, 모든 시간별로 나열하지 않고, 중요한 시점에 집중하여 **전체적인 추세**를 파악할 수 있도록 설명.
    - 강수확률이 30% 이하라면 강수확률이 매우 낮은 것입니다.
    
    ### **일별**
    - 일별 예보를 분석하여, 문장으로만 설명, 모든 일별로 나열하지 않고, 중요한 시점에 집중하여 **전체적인 추세**를 파악할 수 있도록 설명.
    
    ### **대기질**
    - 대기질을 분석하여, 문장으로만 설명.

    ### **안내**
    - 날씨 데이터를 종합적으로 분석하여, 사용자에게 실용적인 조언을 최소 세 가지 제공합니다.
    
    """.trimIndent()
    }

    override fun build(): String = WeakReference(StringBuilder()).get()?.run {
        appendLine(model.currentWeather)
        appendLine()

        appendLine(model.hourlyForecast)
        appendLine()

        appendLine(model.dailyForecast)
        appendLine()

        if (model.airQuality != null) {
            appendLine(model.airQuality)
            appendLine()
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