package io.github.pknujsp.weatherwizard.core.model.weather.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory.Clear
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory.MostlyCloudy
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory.Overcast
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory.PartlyCloudy
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory.Rain
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory.RainAndSnow
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory.Raindrop
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory.RaindropAndSnowBlizzard
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory.Shower
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory.Snow
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory.SnowBlizzard

/**
 * 날씨 상태
 *
 * @param stringRes 날씨 상태에 대한 문자열 리소스
 *
 * @property Clear 맑음
 * @property PartlyCloudy 구름 조금
 * @property MostlyCloudy 구름 많음
 *  @property Overcast 흐림
 *  @property Rain 비
 *  @property RainAndSnow 비/눈
 *  @property Snow 눈
 *  @property Shower 소나기
 *  @property Raindrop 빗방울
 *  @property RaindropAndSnowBlizzard 빗방울/눈날림
 *  @property SnowBlizzard 눈날림
 *
 */
sealed class WeatherConditionCategory(
    @StringRes open val stringRes: Int,
    @DrawableRes open val dayWeatherIcon: Int,
    @DrawableRes open val nightWeatherIcon: Int,
) {


    data object Clear : WeatherConditionCategory(
        io.github.pknujsp.weatherwizard.core.common.R.string.clear,
        io.github.pknujsp.weatherwizard.core.common.R.drawable.day_clear,
        io.github.pknujsp.weatherwizard.core.common.R.drawable.night_clear,
    )


    data object PartlyCloudy : WeatherConditionCategory(
        io.github.pknujsp.weatherwizard.core.common.R.string.partly_cloudy,
        io.github.pknujsp.weatherwizard.core.common.R.drawable.day_partly_cloudy,
        io.github.pknujsp.weatherwizard.core.common.R.drawable.night_partly_cloudy,
    )

    data object MostlyCloudy : WeatherConditionCategory(io.github.pknujsp.weatherwizard.core.common.R.string.mostly_cloudy,
        io.github.pknujsp.weatherwizard.core.common.R.drawable.day_mostly_cloudy,
        io.github.pknujsp.weatherwizard.core.common.R.drawable.night_mostly_cloudy)

    data object Overcast : WeatherConditionCategory(io.github.pknujsp.weatherwizard.core.common.R.string.overcast,
        io.github.pknujsp.weatherwizard.core.common.R.drawable.overcast,
        io.github.pknujsp.weatherwizard.core.common.R.drawable.overcast)

    data object Rain : WeatherConditionCategory(io.github.pknujsp.weatherwizard.core.common.R.string.rain,
        io.github.pknujsp.weatherwizard.core.common.R.drawable.rain,
        io.github.pknujsp.weatherwizard.core.common.R.drawable.rain)

    data object RainAndSnow : WeatherConditionCategory(io.github.pknujsp.weatherwizard.core.common.R.string.rain_and_snow,
        io.github.pknujsp.weatherwizard.core.common.R.drawable.sleet,
        io.github.pknujsp.weatherwizard.core.common.R.drawable.sleet)

    data object Snow : WeatherConditionCategory(io.github.pknujsp.weatherwizard.core.common.R.string.snow,
        io.github.pknujsp.weatherwizard.core.common.R.drawable.snow,
        io.github.pknujsp.weatherwizard.core.common.R.drawable.snow)

    data object Shower : WeatherConditionCategory(io.github.pknujsp.weatherwizard.core.common.R.string.shower,
        io.github.pknujsp.weatherwizard.core.common.R.drawable.showers,
        io.github.pknujsp.weatherwizard.core.common.R.drawable.showers)

    data object Raindrop : WeatherConditionCategory(io.github.pknujsp.weatherwizard.core.common.R.string.raindrop,
        io.github.pknujsp.weatherwizard.core.common.R.drawable.raindrop,
        io.github.pknujsp.weatherwizard.core.common.R.drawable.raindrop)

    data object RaindropAndSnowBlizzard :
        WeatherConditionCategory(io.github.pknujsp.weatherwizard.core.common.R.string.raindrop_and_snow_blizzard,
            io.github.pknujsp.weatherwizard.core.common.R.drawable.sleet,
            io.github.pknujsp.weatherwizard.core.common.R.drawable.sleet)

    data object SnowBlizzard : WeatherConditionCategory(io.github.pknujsp.weatherwizard.core.common.R.string.snow_blizzard,
        io.github.pknujsp.weatherwizard.core.common.R.drawable.snow,
        io.github.pknujsp.weatherwizard.core.common.R.drawable.snow)
}


/*
KMA
    hourly -
        <item>맑음</item>
        <item>구름 많음</item>
        <item>흐림</item>
        <item>비</item>
        <item>비/눈</item>
        <item>눈</item>
        <item>소나기</item>
        <item>빗방울</item>
        <item>빗방울/눈날림</item>
        <item>눈날림</item>

    mid -
        <item>맑음</item>
        <item>구름많음</item>
        <item>구름많고 비</item>
        <item>구름많고 눈</item>
        <item>구름많고 비/눈</item>
        <item>구름많고 소나기</item>
        <item>흐림</item>
        <item>흐리고 비</item>
        <item>흐리고 눈</item>
        <item>흐리고 비/눈</item>
        <item>흐리고 소나기</item>
        <item>소나기</item>

*/