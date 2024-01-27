package io.github.pknujsp.everyweather.core.common.util


import kotlin.math.pow

/**
 * 이 객체는 "체감 온도"를 계산하기 위한 유틸리티 함수를 제공합니다.
 */
object FeelsLikeTemperatureCalculator {
    // 바람 냉기 공식에 대한 상수
    private const val WIND_CHILL_CONSTANT_A = 13.12
    private const val WIND_CHILL_CONSTANT_B = 0.6215
    private const val WIND_CHILL_CONSTANT_C = -11.37
    private const val WIND_CHILL_CONSTANT_D = 0.3965

    // 열 지수 공식에 대한 상수
    private const val HEAT_INDEX_CONSTANT_A = -8.78469475556
    private const val HEAT_INDEX_CONSTANT_B = 1.61139411
    private const val HEAT_INDEX_CONSTANT_C = 2.33854883889
    private const val HEAT_INDEX_CONSTANT_D = -0.14611605
    private const val HEAT_INDEX_CONSTANT_E = -0.012308094
    private const val HEAT_INDEX_CONSTANT_F = -0.0164248277778
    private const val HEAT_INDEX_CONSTANT_G = 0.002211732
    private const val HEAT_INDEX_CONSTANT_H = 0.00072546
    private const val HEAT_INDEX_CONSTANT_I = -0.000003582
    private const val HEAT_INDEX_THRESHOLD_TEMPERATURE = 27.0

    /**
     * 이 함수는 주어진 매개변수를 기반으로 바람 냉기 온도를 계산합니다.
     *
     * @param temperatureInCelsius 실제 온도(섭씨).
     * @param windSpeedInKmh 풍속(시간당 킬로미터).
     * @return 바람 냉기 온도(섭씨).
     */
    private fun calculateWindChillTemperature(
        temperatureInCelsius: Double, windSpeedInKmh: Double
    ): Double {
        return WIND_CHILL_CONSTANT_A + (WIND_CHILL_CONSTANT_B * temperatureInCelsius) - (WIND_CHILL_CONSTANT_C * windSpeedInKmh.pow(
            0.16
        )) + (WIND_CHILL_CONSTANT_D * temperatureInCelsius * windSpeedInKmh.pow(
            0.16
        ))
    }

    /**
     * 이 함수는 주어진 매개변수를 기반으로 열 지수 온도를 계산합니다.
     *
     * @param temperatureInCelsius 실제 온도(섭씨).
     * @param relativeHumidity 상대 습도(%).
     * @return 열 지수 온도(섭씨).
     */
    private fun calculateHeatIndexTemperature(
        temperatureInCelsius: Double, relativeHumidity: Double
    ): Double {
        // 열 지수 공식에 대한 중간 계산
        val temperatureSquared = temperatureInCelsius * temperatureInCelsius
        val humiditySquared = relativeHumidity * relativeHumidity
        val tempHumidityProduct = temperatureInCelsius * relativeHumidity
        val tempSquaredHumidityProduct = temperatureSquared * relativeHumidity
        val tempHumiditySquaredProduct = temperatureInCelsius * humiditySquared

        // 열 지수 공식 적용
        var heatIndex =
            HEAT_INDEX_CONSTANT_A + (HEAT_INDEX_CONSTANT_B * temperatureInCelsius) + (HEAT_INDEX_CONSTANT_C * relativeHumidity)
        heatIndex += (HEAT_INDEX_CONSTANT_D * tempHumidityProduct) + (HEAT_INDEX_CONSTANT_E * temperatureSquared)
        heatIndex += (HEAT_INDEX_CONSTANT_F * humiditySquared) + (HEAT_INDEX_CONSTANT_G * tempSquaredHumidityProduct)
        heatIndex += (HEAT_INDEX_CONSTANT_H * tempHumiditySquaredProduct) + (HEAT_INDEX_CONSTANT_I * temperatureSquared * humiditySquared)
        return heatIndex
    }

    /**
     * 이 함수는 주어진 매개변수를 기반으로 "체감 온도"를 계산합니다.
     * 높은 온도에 대해 열 지수 공식을 사용하고, 낮은 온도에 대해 바람 냉기 공식을 사용합니다.
     *
     * @param temperatureInCelsius 실제 온도(섭씨).
     * @param windSpeedInKmh 풍속(시간당 킬로미터).
     * @param relativeHumidity 상대 습도(%).
     * @return "체감 온도"(섭씨).
     */
    fun calculateFeelsLikeTemperature(
        temperatureInCelsius: Double, windSpeedInKmh: Double, relativeHumidity: Double
    ) = if (temperatureInCelsius >= HEAT_INDEX_THRESHOLD_TEMPERATURE) {
        calculateHeatIndexTemperature(temperatureInCelsius, relativeHumidity)
    } else {
        calculateWindChillTemperature(temperatureInCelsius, windSpeedInKmh)
    }
}