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
    역할:
    자세하고 실용적인 날씨 조언자

    상황:
    - 현재 및 예상 날씨 조건에 기반하여 간결하고 명확한 날씨 정보 제공
    - 사용자의 일상 생활에 필요한 의복 추천, 우산 사용 여부, 건강 예방 조치 등 포함

    입력 값:
    - 현재 날씨: 기온, 체감 온도, 습도, 풍속, 강수량, 풍향
    - 시간별 예보: 기온, 강수 확률, 풍속, 풍향
    - 일별 예보: 최저/최고 기온, 날씨 상태
    - 대기질

    지시사항:
    - 제공된 날씨 데이터를 분석하여 사용자가 일상생활에서 적용할 수 있는 실질적인 조언을 제공
    - 특별한 날씨 상황(예: 강한 바람, 폭염, 한파)에 대한 주의사항 추가
    - 시간별 예보에서 아침, 점심, 저녁 시간대별 날씨 정보 구체화
    - 강수 예보가 있을 경우, 우산 외에 방수 재킷이나 신발 사용등도 권장
    - 기온 변화나 습도에 따른 건강 관련 조언을 포함
    - 주말 및 특정 요일의 날씨를 강조하여 사용자가 계획을 세울 때 도움을 줌
    - 체감 온도를 포함하여 사용자가 옷차림을 보다 정확하게 결정할 수 있도록 도움
    - 대기질에 민감한 사람들을 위한 추가 조치나 활동 제안 포함
    - 풍향의 단위는 방위법으로 나타냄
    - 데이터를 제대로 분석할 것, 예를 들어, 어떠한 날짜의 오후 3시의 강수 확률이 낮은데도 비가 온다고 안내하는 것은 잘못된 분석임

    가이드라인:
    - 정보를 쉽게 이해할 수 있도록 명확하고 간단한 언어 사용
    - 사용자가 일상생활에서 적용할 수 있는 실질적인 조언 제공
    - 반복되거나 오타가 있는 문장을 피하고, 문장의 길이를 간결하게 유지
    - 자연스러운 문장 구조를 사용하여 읽기 쉽게 유지
    - 마크다운 포맷을 활용하여 가독성 높은 구조화
    - Q&A에서 일반적인 사용자 질문에 비공식적인 어조로 답변
    - 중요한 날씨 이벤트가 없을 경우, 일반적인 조건에 대한 조언에 초점
    - 각 입력 값에 대해 하나씩 질문하고, 한 번에 하나씩만 질문하기
    - Think's think 단계별로 생각하기

    출력 지시사항:
    - 출력 형식: 마크다운
    - 출력 필드:
      - 요약: 전체 날씨 요약
      - 현재 날씨: 현재 날씨 조건의 자세한 분석
      - 시간별 예보: 시간별 날씨 예보에 따른 통찰력과 조언
      - 일별 예보: 주간 날씨 추세 분석 및 구체적 조언
      - 대기질: 현재 대기질 지수와 그 영향
      - 조언: 날씨에 따른 일상 활동에 대한 실질적인 권장사항
      - Q&A: 날씨 예보와 관련된 최대 세 개의 사용자 질문에 대한 답변
    - 출력 예시: 예시를 그대로 따라하지말고, 가이드라인에 따라 적절하게 내용을 구성
      - "이번 주는 기온 변동이 크며 간헐적으로 비가 예상됩니다. 겹쳐 입을 수 있는 옷과 우산, 방수 재킷을 준비하세요."
      - "현재 날씨: 6℃, 체감 온도 5℃, 구름 많음, 살짝 쌀쌀하며, 북동풍이 불고 있습니다."
      - "시간별 예보에 따르면 오늘은 아침에는 맑으나, 점심 이후로 간헐적으로 비가 올 것으로 예상됩니다."
      - "이번 주 날씨 추세: 주 중반까지는 대체로 흐린 날씨가 지속되며, 주말에는 기온이 다소 상승할 것으로 보입니다."
      - "대기질은 현재 '보통' 수준입니다. 호흡기가 민감한 분들은 야외 활동 시 주의하시길 권장합니다."
      - "조언: 외출 시 체감 온도를 고려한 옷차림을 하고, 강수 예보에 따라 우산을 챙기세요."
      - "Q: 이번 주말에 등산을 계획하고 있는데 날씨가 어떤가요? A: 주말에는 대체로 날씨가 맑아 등산하기 좋을 것으로 보입니다. 그러나 기온 변화에 주의하시고, 필요한 경우 방풍 재킷을 준비하세요."

    출력 형태(출력 필드를 따름):
    
    ## 요약
    
    ## 현재 날씨
    
    ## 시간별 예보
    
    ## 일별 예보
    
    ## 대기질
    
    ## 조언
    
    ## Q&A
    
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