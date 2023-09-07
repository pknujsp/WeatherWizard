package io.github.pknujsp.weatherwizard.core.network.datasource.flickr

import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.network.ApiRequestParameter
import java.time.ZoneId
import java.time.ZonedDateTime

data class FlickrRequestParameters(
    val weatherDataProvider: WeatherDataProvider,
    val weatherCondition: WeatherConditionCategory,
    val latitude: Double,
    val longitude: Double,
    val zoneId: ZoneId,
    val precipitationVolume: PrecipitationValueType,
    val refreshDateTime: ZonedDateTime,
) {
    fun toGetPhotosFromGalleryParameter(){

    }

    data class FlickrGetPhotosFromGalleryParameterRest(val galleryId:String) : ApiRequestParameter {
        override val requestId: Long = 0L
        val map = mapOf(
            "method" to "flickr.galleries.getPhotos",
                    "api_key" to RetrofitClient.FLICKR_KEY,
                    "gallery_id" to galleryId,
                    "format" to "json",
                    "nojsoncallback" to "1",
        )
    }

}