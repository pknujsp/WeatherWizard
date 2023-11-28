package io.github.pknujsp.weatherwizard.core.data.notification.ongoing.model

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OngoingNotificationSettingsJsonEntity(
    @SerialName("latitude") private val latitude: Double,
    @SerialName("longitude") private val longitude: Double,
    @SerialName("addressName") private val addressName: String,
    @SerialName("locationType") private val locationType: Int,
    @SerialName("refreshInterval") private val refreshInterval: Int,
    @SerialName("weatherProvider") private val weatherProvider: Int,
    @SerialName("notificationIconType") private val notificationIconType: Int,
) : EntityModel {
    fun getLocationType() = LocationType.fromKey(locationType).let {
        if (it is LocationType.CustomLocation) {
            LocationType.CustomLocation(0, latitude, longitude, addressName)
        } else {
            it
        }
    }

    fun getRefreshInterval() = RefreshInterval.fromKey(refreshInterval)
    fun getWeatherProvider() = WeatherDataProvider.fromKey(weatherProvider)
    fun getNotificationIconType() = NotificationIconType.fromKey(notificationIconType)
}