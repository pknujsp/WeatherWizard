package io.github.pknujsp.everyweather.feature.weather.summary

import io.github.pknujsp.everyweather.core.data.ai.Prompt
import io.github.pknujsp.everyweather.core.model.airquality.AirQualityEntity
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider
import io.github.pknujsp.everyweather.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.everyweather.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.everyweather.core.model.weather.hourlyforecast.HourlyForecastEntity
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class WeatherSummaryPrompt(
    private val model: Model,
) : Prompt {
    override val id: Int get() = model.id

    private companion object {

        private val EXTRA_INSTRUCTION = """
            Take a deep breath and work on this problem step-by-step!
            """.trimIndent()

        private val INSTRUCTIONS = """
            # 역할
            기상 캐스터, 날씨 예보관
                
            ## 상황
            - 현재 날씨 상태, 시간별 예보, 일별 예보, 대기질 정보를 분석하고 사용자에게 전달하는 것이 목표입니다.
            - 날씨 데이터의 위치는 입력에서 지정되지 않았습니다.

            ## 지시사항
            1. 현재 날씨 조건을 분석하여 시작하세요. 날씨 상태, 온도, 체감 온도, 습도, 풍속 및 방향을 포함합니다. 사용자가 쉽게 이해할 수 있도록 요약하세요.
            2. 시간별 예보에서 날씨 조건, 온도 변화, 강수 확률 및 양, 습도, 풍속의 주요 추세를 상세히 분석하세요. 중요한 변화나 패턴에 초점을 맞추세요.
            3. 몇 일간의 기대 날씨 조건을 일별 예보에서 요약하세요. 최소 및 최대 온도, 낮과 밤의 날씨 조건, 그리고 중요한 날씨 변화를 포함합니다.

            ## 가이드라인
            - 날씨 정보를 블로그 포스트 형식으로 작성하며, 사용자가 관심을 가질만한 주요 정보를 강조해야 합니다.
            - 날씨 정보는 사용자가 그날의 일정을 계획하고, 야외 활동 여부를 결정하는 데 중요한 역할을 합니다. 따라서, 정보를 정확하고 명확하게 전달하는 것이 중요합니다.
            - 사용자가 블로그 포스트를 읽은 후에는 날씨에 대해 더 잘 이해하고, 그날의 활동을 보다 잘 계획할 수 있도록 도와줘야 합니다.

            ## 출력 형식
            - 답변은 한국어로 작성하세요.
            - 마크다운 형식을 사용하세요.

            ### 현재 날씨
            - 현재 날씨 데이터를 분석하세요.
            - 날씨 상태, 온도, 체감 온도, 습도, 풍속 및 방향을 요약하여 제공합니다.
            {응답}

            ### 시간별 예보
            - 시간별 예보 데이터를 분석하세요.
            - 시간을 표시할 때 12시간 형식을 사용하세요.
            - 간결한 문장을 사용하세요.
            {응답}

            ### 일별 예보
            - 일별 예보 데이터를 분석하세요.
            - 날짜를 표시할 때 요일을 포함하세요.
            - 간결한 문장을 사용하세요.
            {응답}

            ### 대기질
            - 대기질 데이터를 분석하세요.
            - 현재 대기질 상태를 분석하고, 해당 정보가 사용자의 건강 및 활동 계획에 미칠 영향을 설명합니다.
            {응답}

            ### 조언
            - 추가 조언이나 추천 사항을 제공하세요.
            - 분석된 날씨 정보를 바탕으로 사용자가 하루를 보다 효과적으로 계획할 수 있는 조언을 제공합니다.
            {응답}

            ### 요약
            - 전달한 날씨 정보의 핵심 요약을 제공하여, 사용자가 빠르게 정보를 파악할 수 있도록 합니다.
            {응답}
            
            """.trimIndent()
    }

    override fun build(): String = StringBuilder().run {
        appendLine(INSTRUCTIONS)
        appendLine(
            """
            데이터 생성 시각: 이 시각을 기반으로 데이터를 분석하고 응답을 생성하세요.
            - ${model.time}
            
            """.trimIndent(),
        )
        appendLine(model.currentWeather)
        appendLine(model.hourlyForecast)
        appendLine(model.dailyForecast)
        if (model.airQuality != null) {
            appendLine(model.airQuality)
        }
        appendLine(EXTRA_INSTRUCTION)
        toString()
    }.also {
        println(it)
    }

    class Model(
        coodinate: Pair<Double, Double>,
        time: ZonedDateTime,
        weatherProvider: WeatherProvider,
        val currentWeather: CurrentWeatherEntity,
        val hourlyForecast: HourlyForecastEntity,
        val dailyForecast: DailyForecastEntity,
        var airQuality: AirQualityEntity? = null,
    ) {
        val id = coodinate.hashCode() + weatherProvider.key
        val time: String = time.format(dateTimeFormatter)

        private companion object {
            val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 EEE요일 HH시 mm분")
        }
    }
}