package io.github.pknujsp.weatherwizard.core.model.weather.common


import androidx.annotation.StringRes
import io.github.pknujsp.weatherwizard.core.model.R

enum class WeatherDataCategory(@StringRes val stringId: Int) {
    // 기온
    TEMPERATURE(R.string.temperature),

    // 최저 기온
    MIN_TEMPERATURE(R.string.min_temperature),

    // 최고 기온
    MAX_TEMPERATURE(R.string.max_temperature),

    // 체감 온도
    FEELS_LIKE_TEMPERATURE(R.string.feels_like_temperature),

    // 풍속
    WIND_SPEED(R.string.wind_speed),

    // 최대 풍속
    MAX_WIND_SPEED(R.string.max_wind_speed),

    // 풍향
    WIND_DIRECTION(R.string.wind_direction),

    // 강수량
    PRECIPITATION(R.string.precipitation),

    // 총 강수량
    TOTAL_PRECIPITATION(R.string.total_precipitation),

    // 강우량
    RAINFALL(R.string.rainfall),

    // 적설량
    SNOWFALL(R.string.snowfall),

    // 시정거리
    VISIBILITY(R.string.visibility),

    // 최소 시정거리
    MIN_VISIBILITY(R.string.min_visibility),

    // 대기압
    PRESSURE(R.string.pressure),

    // 상대 습도
    HUMIDITY(R.string.humidity),

    // 자외선 지수
    UV_INDEX(R.string.uv_index),

    // 오존 농도
    OZONE(R.string.ozone),

    // 대기질 지수
    AIR_QUALITY_INDEX(R.string.air_quality_index),

    // 미세먼지(PM2.5) 농도
    PM25(R.string.pm25),

    // 초미세먼지(PM10) 농도
    PM10(R.string.pm10),

    // 일출 시간
    SUNRISE(R.string.sunrise),

    // 일몰 시간
    SUNSET(R.string.sunset),

    // 달뜨는 시간
    MOONRISE(R.string.moonrise),

    // 달지는 시간
    MOONSET(R.string.moonset),

    // 현재 날씨 상태(맑음, 흐림 등)
    WEATHER_CONDITION(R.string.weather_condition);

    companion object {
        fun WeatherDataCategory.stringId(): Int = stringId
    }
}