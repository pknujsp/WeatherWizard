package io.github.pknujsp.weatherwizard.core.model.notification.daily

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime


@Serializable
class DailyNotificationInfoEntity(
    @SerialName("latitude") val latitude: Double = 0.0,
    @SerialName("longitude") val longitude: Double = 0.0,
    @SerialName("addressName") val addressName: String = "",
    @SerialName("hour") val hour: Int = 12,
    @SerialName("minute") val minute: Int = 0,
    @SerialName("locationType") private val locationType: Int = LocationType.CurrentLocation.key,
    @SerialName("weatherProvider") private val weatherProvider: Int = WeatherDataProvider.default.key,
    @SerialName("type") private val type: Int = DailyNotificationType.default.key,
    @SerialName("createdDateTime") private val createdDateTimeISO8601: String = ZonedDateTime.now().toString(),
) : EntityModel {
    fun getLocationType(): LocationType = LocationType.fromKey(locationType).let {
        if (it is LocationType.CustomLocation) {
            LocationType.CustomLocation(latitude = latitude, longitude = longitude, address = addressName)
        } else {
            it
        }
    }

    fun getWeatherProvider(): WeatherDataProvider = WeatherDataProvider.fromKey(weatherProvider)

    fun getType(): DailyNotificationType = DailyNotificationType.fromKey(type)

}