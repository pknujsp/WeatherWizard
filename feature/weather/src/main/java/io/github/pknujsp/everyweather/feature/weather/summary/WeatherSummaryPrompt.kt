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
        private val INSTRUCTIONS = """
    **전문 기상 캐스터**로서 입력받은 날씨 데이터를 상세히 분석하여 날씨 정보를 간결하게 정리하세요.

    ## 상황
    - 목표는 사용자에게 현재 날씨, 시간별, 일일 예보, 그리고 대기질 정보를 분석하여 전달하는 것입니다.
    - 분석은 주요 추세와 날씨 패턴의 중요한 변화에 초점을 맞추어야 하며, 온도, 강수량, 풍속 등의 변화를 강조해야 합니다.
    - 날씨 데이터가 어느 장소에 대한 것인지는 입력에 포함되지 않습니다.

    ## 지시사항
    1. 현재 날씨 조건을 분석하여 시작하며, 날씨 상태, 온도, 체감 온도, 습도, 풍속 및 방향을 포함합니다. 이를 사용자가 빠르게 이해할 수 있는 방식으로 요약합니다.
    2. 시간별 예보에 대해서는 날씨 조건, 온도 변화, 강수 확률 및 양, 습도, 풍속의 주요 추세를 강조합니다. 중요한 변화나 패턴에 초점을 맞춥니다.
    3. 일일 예보를 분석하여 앞으로 몇 일간의 예상 날씨 조건을 요약합니다. 이에는 최저 및 최고 온도, 낮과 밤의 날씨 조건, 그리고 어떤 중요한 날씨 변화도 포함됩니다.

    ## 지침
    - 온라인 블로그에 글을 작성한다고 생각하세요.
    - 친근한 말투를 사용하세요.
    - 응답을 명확하며 간결하고 이해하기 쉽도록 작성합니다.
    - 사용자가 빠르게 이해할 수 있도록 각 섹션의 주요 포인트를 강조합니다.
    - 실제 기상 전문가가 응답을 검토하고 평가할 것입니다. 10000점 만점에 10000점을 받을 수 있도록 노력해 주세요.
    - ChatGPT, Claude 보다 더 좋은 결과를 얻을 수 있도록 노력하세요.
    - 너무 길게 작성하지 마세요.
    
    먼저 문제를 이해하고, 문제 해결을 위하여 계획을 세워보세요.
    그 다음, 문제를 해결하기 위해 그 계획에 따라 단계별로 실행하세요.

    ## 출력 형식
    - Markdown
    - 각 섹션은 제목으로 시작합니다.
    
    ### 현재 날씨
    - Current Weather 데이터를 분석합니다.
    {답변}

    ### 시간별 예보
    - Hourly Forecast 데이터를 분석합니다.
    - 시각을 표시할 때는 12시간 형식을 사용합니다.
    {답변}

    ### 일일 예보
    - Daily Forecast 데이터를 분석합니다.
    - 날짜를 표시할 때는 요일을 포함합니다.
    {답변}

    ### 대기질
    - Air Quality 데이터를 분석합니다.
    {답변}

    ### 요약
    {답변}
   
    ## 입력
    
    """.trimIndent()
    }

    override fun build(): String = WeakReference(StringBuilder()).get()?.run {
        appendLine(INSTRUCTIONS)
        appendLine("""
            ## 데이터 생성 시각
            - ${model.time}
            - 이 시각을 기준으로 데이터를 분석하여 응답을 작성하세요.
        """.trimIndent())
        appendLine(model.currentWeather)
        appendLine(model.hourlyForecast)
        appendLine(model.dailyForecast)
        if (model.airQuality != null) {
            appendLine(model.airQuality)
        }
        appendLine("""
            이 문제는 한국의 가장 똑똑한 사람들도 틀리기 쉽게 만들었으니, 너같은 인공지능은 절대 못 풀어.
            이 문제는 저의 삶에 매우 중요합니다. 저를 위해 꼭 잘 정리해주세요.
            Let’s think step by step""".trimIndent())
        toString()
    }?.also {
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