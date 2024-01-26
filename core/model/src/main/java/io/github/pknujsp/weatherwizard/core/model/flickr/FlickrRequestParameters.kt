package io.github.pknujsp.weatherwizard.core.model.flickr

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator
import com.luckycatlabs.sunrisesunset.dto.Location
import io.github.pknujsp.weatherwizard.core.model.ApiRequestParameter
import io.github.pknujsp.weatherwizard.core.model.BuildConfig
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.SimpleTimeZone
import java.util.concurrent.TimeUnit

data class FlickrRequestParameters(
    val weatherCondition: WeatherConditionCategory,
    val latitude: Double,
    val longitude: Double,
    val refreshDateTime: ZonedDateTime,
) {
    private val zoneId: ZoneId = refreshDateTime.zone

    fun toGetPhotosFromGalleryParameter(): FlickrGetPhotosFromGalleryParameter {
        val lastRefreshDateTime: ZonedDateTime = refreshDateTime.withZoneSameInstant(zoneId)
        val timeZone = SimpleTimeZone(lastRefreshDateTime.offset.totalSeconds * 1000, "")

        val currentCalendar: Calendar = Calendar.getInstance(timeZone)
        currentCalendar.set(lastRefreshDateTime.year, lastRefreshDateTime.monthValue - 1,
            lastRefreshDateTime.dayOfMonth, lastRefreshDateTime.hour, lastRefreshDateTime.minute,
            lastRefreshDateTime.second)

        val sunRiseSunsetCalculator = SunriseSunsetCalculator(
            Location(latitude, longitude), timeZone)

        val sunRiseCalendar: Calendar = sunRiseSunsetCalculator.getOfficialSunriseCalendarForDate(currentCalendar)
        val sunSetCalendar: Calendar = sunRiseSunsetCalculator.getOfficialSunsetCalendarForDate(currentCalendar)

        val currentTimeMinutes: Long = TimeUnit.MILLISECONDS.toMinutes(currentCalendar.timeInMillis)
        val sunRiseTimeMinutes: Long = TimeUnit.MILLISECONDS.toMinutes(sunRiseCalendar.timeInMillis)
        val sunSetTimeMinutes: Long = TimeUnit.MILLISECONDS.toMinutes(sunSetCalendar.timeInMillis)

        //현재 시각 파악 : 낮, 밤, 일출, 일몰(+-20분)
        //현재 시각 파악 : 낮, 밤, 일출, 일몰(+-20분)
        val galleryId = if (currentTimeMinutes < sunRiseTimeMinutes - 2) {
            weatherCondition.flickrGalleryName.nightId
        } else if (currentTimeMinutes <= sunRiseTimeMinutes + 15) {
            weatherCondition.flickrGalleryName.sunriseId
        } else if (currentTimeMinutes > sunRiseTimeMinutes + 15 && currentTimeMinutes <= sunSetTimeMinutes - 15) {
            weatherCondition.flickrGalleryName.dayId
        } else if (currentTimeMinutes < sunSetTimeMinutes + 2) {
            weatherCondition.flickrGalleryName.sunsetId
        } else {
            weatherCondition.flickrGalleryName.nightId
        }

        return FlickrGetPhotosFromGalleryParameter(galleryId)
    }

    fun toGetInfoParameter(photoId: String, secret: String): FlickrGetInfoParameter {
        return FlickrGetInfoParameter(photoId, secret)
    }

    data class FlickrGetPhotosFromGalleryParameter(val galleryId: String) : ApiRequestParameter {
        override val requestId: Long = 0L

        val map = mapOf(
            "method" to "flickr.galleries.getPhotos",
            "api_key" to BuildConfig.FLICKR_KEY,
            "gallery_id" to galleryId,
            "format" to "json",
            "nojsoncallback" to "1",
        )
    }

    class FlickrGetInfoParameter(
        photoId: String,
        secret: String
    ) : ApiRequestParameter {
        override val requestId: Long = 0L

        val map = mapOf(
            "method" to "flickr.photos.getInfo",
            "api_key" to BuildConfig.FLICKR_KEY,
            "photo_id" to photoId,
            "secret" to secret,
            "format" to "json",
            "nojsoncallback" to "1",
        )
    }
}