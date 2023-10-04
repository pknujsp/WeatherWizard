package io.github.pknujsp.weatherwizard.core.common.util

import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.pow

class WeatherUtil {
    fun calcFeelsLikeTemperature(celsiusTemperature: Double, kmPerHWindSpeed: Double, humidity: Double): Double {
        return if (celsiusTemperature < 11.0) {
            /*
                 - 겨울 체감온도 = 13.12 + 0.6215T - 11.37 V0.16 + 0.3965 V0.16T
                 * T : 기온(℃), V : 풍속(km/h)
                  */
            if (kmPerHWindSpeed <= 4.68) {
                celsiusTemperature
            } else {
                13.12 + 0.6215 * celsiusTemperature - 11.37 * kmPerHWindSpeed.pow(0.16) + 0.3965 * kmPerHWindSpeed.pow(0.16) * celsiusTemperature
            }
        } else {
            /*
                 - 여름 체감온도 = -0.2442 + 0.55399Tw + 0.45535Ta – 0.0022Tw2 + 0.00278TwTa + 3.5
                 * Tw = Ta * ATAN[0.151977(RH+8.313659)1/2] + ATAN(Ta+RH) - ATAN(RH-1.67633) + 0.00391838 * RH * 3/2 * ATAN(0.023101RH) - 4.686035
                 ** Ta : 기온(℃), Tw : 습구온도(Stull의 추정식** 이용), RH : 상대습도(%)
                  */
            val tw = celsiusTemperature * atan(abs(0.151977 * (humidity + 8.313659).pow(0.5))) + atan(celsiusTemperature + humidity)
            -atan(humidity - 1.67633) + 0.00391838 * humidity.pow(1.5) * atan(0.023101 * humidity) - 4.686035
            -0.2442 + 0.55399 * tw + 0.45535 * celsiusTemperature - 0.0022 * tw.pow(2.0) + 0.00278 * tw * celsiusTemperature + 3.5
        }
    }
}