package io.github.pknujsp.weatherwizard.core.model.notification.ongoing

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.notification.RefreshInterval
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
class OngoingNotificationInfoEntity(
    @SerialName("latitude") val latitude: Double = 0.0,
    @SerialName("longitude") val longitude: Double = 0.0,
    @SerialName("addressName") val addressName: String = "",
    @SerialName("locationType") private val locationType: Int = LocationType.CurrentLocation.key,
    @SerialName("refreshInterval") private val refreshInterval: Int = RefreshInterval.MANUAL.key,
    @SerialName("weatherProvider") private val weatherProvider: Int = WeatherDataProvider.default.key,
    @SerialName("notificationIconType") private val notificationIconType: Int = NotificationIconType.TEMPERATURE.key,
    @SerialName("createdDateTime") private val createdDateTimeISO8601: String = ZonedDateTime.now().toString(),
) : EntityModel {
    fun getLocationType(): LocationType = LocationType.fromKey(locationType)

    fun getWeatherProvider(): WeatherDataProvider = WeatherDataProvider.fromKey(weatherProvider)

    fun getNotificationIconType(): NotificationIconType = NotificationIconType.fromKey(notificationIconType)

    fun getRefreshInterval(): RefreshInterval = RefreshInterval.fromKey(refreshInterval)

}