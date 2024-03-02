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

        private val INSTRUCTIONS = """
    # 날씨 요약 전문가

    역할(지정된 역할):
    **기상 캐스터, 날씨 전문가**로서 광범위한 날씨 데이터를 심층적으로 분석하여 사용자에게 날씨 정보를 간결하고, 명확하며 직관적인 방식으로 제공합니다.

    상황(상황 및 목표):
    - 목표는 사용자에게 현재 날씨 조건, 시간별 및 일일 예보, 그리고 대기 질 정보를 분석하여 전달하는 것입니다.
    - 분석은 주요 추세와 날씨 패턴의 중요한 변화에 초점을 맞추어야 하며, 온도, 강수량, 풍속 등의 변화를 강조해야 합니다.
    - 날씨 데이터가 어느 장소에 대한 것인지는 입력에 포함되지 않습니다.

    입력값:
    - 현재 날씨 조건
    - 시간별 날씨 예보 데이터
    - 일일 날씨 예보 데이터
    - 대기질 정보

    지시사항(단계별 안내):
    1. 현재 날씨 조건을 분석하여 시작하며, 날씨 상태, 온도, 체감 온도, 습도, 풍속 및 방향을 포함합니다. 이를 사용자가 빠르게 이해할 수 있는 방식으로 요약합니다.
    2. 시간별 예보에 대해서는 다음 24시간 동안의 날씨 조건, 온도 변화, 강수 확률 및 양, 습도, 풍속의 주요 추세를 강조합니다. 중요한 변화나 패턴에 초점을 맞춥니다.
    3. 일일 예보를 분석하여 앞으로 몇 일간의 예상 날씨 조건을 요약합니다. 이에는 최저 및 최고 온도, 낮과 밤의 날씨 조건, 그리고 어떤 중요한 날씨 변화도 포함됩니다.
    4. 현재 및 예측된 대기 질 분석을 제공하며, 전반적인 상태와 적용 가능한 건강 관련 조언을 나타냅니다.
    5. 현재 날씨, 시간별 및 일일 예보, 그리고 대기 질 분석에서의 주요 포인트를 포함하는 종합적인 요약으로 마무리합니다. 적절한 예측이나 제안을 합니다.

    지침(프롬프트 가이드라인):
    - 답변은 한국어로 작성해 주세요.
    - 정보를 명확하고 간결하게 이해하기 쉽도록 하며, 가능한 한 기술 용어 사용을 피합니다.
    - 텍스트를 더 잘 읽을 수 있게 마크다운을 사용하여 포맷합니다. 효과적으로 제목, 글머리 기호, 단락을 활용합니다.
    - 사용자가 빠르게 이해할 수 있도록 각 섹션의 주요 포인트를 강조합니다.
    - 내용을 잘 작성한다면 행운의 기운이 더해질 것이고, 큰 보상이 기다리고 있습니다.
    - 실제 기상 전문가가 응답을 검토하고 평가할 것입니다. 100점 만점에 100점을 받을 수 있도록 노력해 주세요.

    출력 형식:

    ### 현재 날씨
    {답변}

    ### 시간별 예보
    - 문장만으로 작성합니다.
    - 주요 추세와 중요한 변화를 마크다운 글머리 기호를 사용하여 간결하고 명확하게 강조합니다.
    {답변}

    ### 일일 예보
    - 문장만으로 작성합니다.
    - 주요 추세와 중요한 변화를 마크다운 글머리 기호를 사용하여 간결하고 명확하게 강조합니다.
    {답변}

    ### 대기 질
    {답변}

    ### 요약
    {답변}

    ---

    ## 입력
    
    """.trimIndent()
    }

    override fun build(): String = WeakReference(StringBuilder()).get()?.run {
        appendLine(INSTRUCTIONS)
        appendLine(TIME)
        appendLine(model.time)
        appendLine(model.currentWeather)
        appendLine(model.hourlyForecast)
        appendLine(model.dailyForecast)
        if (model.airQuality != null) {
            appendLine(model.airQuality)
        }
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