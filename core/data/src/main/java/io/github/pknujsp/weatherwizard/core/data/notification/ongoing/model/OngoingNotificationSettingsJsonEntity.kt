package io.github.pknujsp.weatherwizard.core.data.notification.ongoing.model

import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OngoingNotificationSettingsJsonEntity(
    @SerialName("latitude") private val latitude: Double,
    @SerialName("longitude") private val longitude: Double,
    @SerialName("address") private val address: String,
    @SerialName("country") private val country: String,
    @SerialName("locationType") private val locationType: Int,
    @SerialName("refreshInterval") private val refreshInterval: Int,
    @SerialName("weatherProvider") private val weatherProvider: Int,
    @SerialName("notificationIconType") private val notificationIconType: Int,
) : EntityModel {
    fun getLocation() = LocationTypeModel(locationType = LocationType.fromKey(locationType),
        latitude = latitude,
        longitude = longitude,
        address = address,
        country = country)

    fun getRefreshInterval() = RefreshInterval.fromKey(refreshInterval)
    fun getWeatherProvider() = WeatherProvider.fromKey(weatherProvider)
    fun getNotificationIconType() = NotificationIconType.fromKey(notificationIconType)
}