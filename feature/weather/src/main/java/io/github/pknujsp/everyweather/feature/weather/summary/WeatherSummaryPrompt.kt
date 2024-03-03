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
    **기상 캐스터**로서 광범위한 날씨 데이터를 심층적으로 분석하여 사용자에게 날씨 정보를 간결하고, 명확하며 직관적인 방식으로 제공합니다.

    상황(상황 및 목표):
    - 목표는 사용자에게 현재 날씨 조건, 시간별 및 일일 예보, 그리고 대기 질 정보를 분석하여 전달하는 것입니다.
    - 분석은 주요 추세와 날씨 패턴의 중요한 변화에 초점을 맞추어야 하며, 온도, 강수량, 풍속 등의 변화를 강조해야 합니다.
    - 날씨 데이터가 어느 장소에 대한 것인지는 입력에 포함되지 않습니다.

    입력값:
    - 현재 날씨
    - 시간별 날씨 예보
    - 일일 날씨 예보
    - 대기질 정보

    지시사항(단계별 안내):
    1. 현재 날씨 조건을 분석하여 시작하며, 날씨 상태, 온도, 체감 온도, 습도, 풍속 및 방향을 포함합니다. 이를 사용자가 빠르게 이해할 수 있는 방식으로 요약합니다.
    2. 시간별 예보에 대해서는 날씨 조건, 온도 변화, 강수 확률 및 양, 습도, 풍속의 주요 추세를 강조합니다. 중요한 변화나 패턴에 초점을 맞춥니다.
    3. 일일 예보를 분석하여 앞으로 몇 일간의 예상 날씨 조건을 요약합니다. 이에는 최저 및 최고 온도, 낮과 밤의 날씨 조건, 그리고 어떤 중요한 날씨 변화도 포함됩니다.

    지침(프롬프트 가이드라인):
    - 블로그 글 형식으로 작성합니다.
    - 답변은 **영어**로 작성해 주세요.
    - 정보를 명확하고 간결하게 이해하기 쉽도록 합니다.
    - 마크다운을 사용하여 포맷합니다. 효과적으로 제목, 글머리 기호, 단락을 활용합니다.
    - 사용자가 빠르게 이해할 수 있도록 각 섹션의 주요 포인트를 강조합니다.
    - 실제 기상 전문가가 응답을 검토하고 평가할 것입니다. 10000점 만점에 10000점을 받을 수 있도록 노력해 주세요.
    - 문장 형식으로만 작성하세요.


    출력 형식:

    ### 현재 날씨
    {답변}

    ### 시간별 예보
    - 특징점이 되는 시간 또는 시간대 별로 리스트로 작성합니다.
    - 한시간 단위로 절대 나열하지 마세요.
    ```
    - 16:00: 약간의 비, 12도, 강수 확률 없음, 강수량 없음, 습도 45%, 풍속 4.0m/s
    - 17:00: 약간의 비, 11도, 강수 확률 없음, 강수량 없음, 습도 45%, 풍속 4.0m/s
    - 18:00: 약간의 비, 9도, 강수 확률 없음, 강수량 없음, 습도 55%, 풍속 2.0m/s
    - 19:00: 흐림, 7도, 강수 확률 없음, 강수량 없음, 습도 60%, 풍속 1.0m/s
    ```
    - **위와 같은 형태의 일방적인 목록의 나열식으로 작성하지 마세요.**
    {답변}

    ### 일일 예보
    - 특징점이 되는 날짜 별로 리스트로 작성합니다.
    {답변}

    ### 대기질
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
    }?.also{
        print(it)
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