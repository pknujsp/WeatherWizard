package io.github.pknujsp.weatherwizard.core.model.weather.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.github.pknujsp.weatherwizard.core.model.flickr.FlickrGalleryId
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
import kotlinx.serialization.Serializable

/**
 * 날씨 상태
 *
 * @param stringRes 날씨 상태에 대한 문자열 리소스
 *
 * @property Clear 맑음
 * @property PartlyCloudy 구름 조금
 * @property MostlyCloudy 구름 많음
 * @property Overcast 흐림
 * @property Rain 비
 * @property RainAndSnow 비/눈
 * @property Snow 눈
 * @property Shower 소나기
 * @property Raindrop 빗방울
 * @property RaindropAndSnowBlizzard 빗방울/눈날림
 * @property SnowBlizzard 눈날림
 *
 */
@Serializable
sealed class WeatherConditionCategory(
    @StringRes open val stringRes: Int,
    @DrawableRes open val dayWeatherIcon: Int,
    @DrawableRes open val nightWeatherIcon: Int,
    open val flickrGalleryName: FlickrGalleryId
) {

    fun getWeatherIconByTimeOfDay(isDay: Boolean): Int = if (isDay) dayWeatherIcon else nightWeatherIcon
    @Serializable
    data object Clear : WeatherConditionCategory(
        io.github.pknujsp.weatherwizard.core.resource.R.string.clear,
        io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_weather_clear,
        io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_full_moon,
        FlickrGalleryId.Clear,
    )

    @Serializable
    data object PartlyCloudy : WeatherConditionCategory(
        io.github.pknujsp.weatherwizard.core.resource.R.string.partly_cloudy,
        io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_partly_cloudy_day,
        io.github.pknujsp.weatherwizard.core.resource.R.drawable.night_partly_cloudy,
        FlickrGalleryId.PartlyCloudy,
    )
    @Serializable
    data object MostlyCloudy : WeatherConditionCategory(
        io.github.pknujsp.weatherwizard.core.resource.R.string.mostly_cloudy,
        io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_clouds,
        io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_clouds,
        FlickrGalleryId.MostlyCloudy,
    )
    @Serializable
    data object Overcast : WeatherConditionCategory(
        io.github.pknujsp.weatherwizard.core.resource.R.string.overcast,
        io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_cloud,
        io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_cloud,
        FlickrGalleryId.Overcast,
    )
    @Serializable
    data object Rain : WeatherConditionCategory(
        io.github.pknujsp.weatherwizard.core.resource.R.string.rain,
        io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_rain,
        io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_rain,
        FlickrGalleryId.Rain,
    )
    @Serializable
    data object RainAndSnow : WeatherConditionCategory(
        io.github.pknujsp.weatherwizard.core.resource.R.string.rain_and_snow,
        io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_sleet,
        io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_sleet,
        FlickrGalleryId.Rain,
    )
    @Serializable
    data object Snow : WeatherConditionCategory(
        io.github.pknujsp.weatherwizard.core.resource.R.string.snow,
        io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_snow,
        io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_snow,
        FlickrGalleryId.Snow,
    )
    @Serializable
    data object Shower : WeatherConditionCategory(
        io.github.pknujsp.weatherwizard.core.resource.R.string.shower,
        io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_rainfall,
        io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_rainfall,
        FlickrGalleryId.Rain,
    )
    @Serializable
    data object Raindrop : WeatherConditionCategory(
        io.github.pknujsp.weatherwizard.core.resource.R.string.raindrop,
        io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_wet,
        io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_wet,
        FlickrGalleryId.Rain,
    )
    @Serializable
    data object RaindropAndSnowBlizzard : WeatherConditionCategory(
        io.github.pknujsp.weatherwizard.core.resource.R.string.raindrop_and_snow_blizzard,
        io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_sleet,
        io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_sleet,
        FlickrGalleryId.Rain,
    )
    @Serializable
    data object SnowBlizzard : WeatherConditionCategory(
        io.github.pknujsp.weatherwizard.core.resource.R.string.snow_blizzard,
        io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_light_snow,
        io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_light_snow,
        FlickrGalleryId.Snow,
    )
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