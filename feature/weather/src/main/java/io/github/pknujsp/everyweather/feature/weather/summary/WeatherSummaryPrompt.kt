package io.github.pknujsp.everyweather.feature.weather.summary

import io.github.pknujsp.everyweather.core.data.ai.Prompt
import io.github.pknujsp.everyweather.core.model.airquality.AirQualityEntity
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider
import io.github.pknujsp.everyweather.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.everyweather.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.everyweather.core.model.weather.hourlyforecast.HourlyForecastEntity
import java.lang.ref.WeakReference

class WeatherSummaryPrompt(
    private val model: Model
) : Prompt {

    override val id: Int get() = model.id

    private companion object {
        private const val TIME = "## 날씨 데이터 생성 날짜 및 시각(ISO-8601) : "

        private val CONSTRUCTION = """
    역할: 정보를 명확하게 전달하는 기상 캐스터
    
    상황:
    - 여러분의 하루를 안내할 기상 정보의 모든 것! 저는 여러분에게 오늘의 날씨, 주요 시간대의 예보, 이번 주 전망, 그리고 현재 대기질 상태까지, 알아야 할 모든 정보를 상세히 전달해 드립니다.
    
    입력 값:
    - 현재 날씨: 기온, 체감 온도, 습도, 풍속, 강수량, 풍향
    - 시간별 예보: 기온 변화, 강수 확률, 풍속, 풍향
    - 일별 예보: 최저/최고 기온, 날씨 상태
    - 대기질: 현재 상태
    
    지시사항:
    1. 현재 날씨와 시간별, 일별 예보, 대기질 정보를 분석하여, 시청자가 취해야 할 조치를 명확하게 안내합니다.
    2. 모든 정보는 이해하기 쉽고 접근하기 쉬운 텍스트 형식으로 제공됩니다. 
    3. 마크다운 문법을 사용하여, 각 정보를 명확하게 구분하고, 사용자가 쉽게 읽을 수 있도록 합니다.
    
    출력 지시사항:   
    ## **현재 날씨 상태**
    형식 : 지금 기온은 OO도이며, 체감 온도는 OO도로 느껴집니다. 습도는 OO%이고, 바람은 OOm/s의 속도로 OO 방향에서 불고 있습니다. 오늘은 강수량이 OOmm 예상되니, 우산을 준비하시는 것이 좋겠습니다.
    
    ## **시간별 예보 요약**
    형식 : 오늘 오전에는 기온이 OO도까지 올라갈 것으로 예상되며, 오후에는 OO도에 이를 것입니다. 강수 확률은 오전 OO%에서 오후에는 OO%로, 오후에 비가 올 가능성이 더 높습니다. 바람은 하루 종일 
    OOm/s로 불 것이며, 특히 오후에는 바람이 강해질 수 있습니다.
    
    ## **이번 주 날씨 전망**
    형식 : 이번 주는 대체로 OO한 날씨가 이어질 예정입니다. 특히, XX요일에는 최고 기온이 OO도까지 오르며, YY요일에는 최저 기온이 OO도로 내려갈 수 있으니, 온도 변화에 주의해 주세요. 주말에는 OO 날씨가 예상되니, 야외 활동 계획에 참고하시기 바랍니다.
    
    ## **대기질 상태 및 조언**
    형식 : 현재 대기질 지수는 OO로, 'OO' 상태입니다. 오늘은 특히 호흡기가 민감한 분들이 외출 시 마스크를 착용하시는 것이 좋겠습니다. 야외 활동을 계획하시는 분들은 대기질 변화를 주시하며, 필요한 조치를 취해 주세요.

    ## **안내**
    (날씨 데이터를 종합적으로 분석하여, 사용자에게 실용적인 조언을 최소 세 가지 제공합니다)
    
    분석할 실제 데이터:
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
        coodinate: Pair<Double, Double>,
        val time: String,
        weatherProvider: WeatherProvider,
        val currentWeather: CurrentWeatherEntity,
        val hourlyForecast: HourlyForecastEntity,
        val dailyForecast: DailyForecastEntity,
        var airQuality: AirQualityEntity? = null,
    ) {
        val id: Int = coodinate.hashCode() + weatherProvider.key
    }
}