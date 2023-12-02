package io.github.pknujsp.weatherwizard.core.data.notification.daily.model

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class DailyNotificationSettingsJsonEntity(
    @SerialName("latitude") private val latitude: Double,
    @SerialName("longitude") private val longitude: Double,
    @SerialName("addressName") private val addressName: String,
    @SerialName("hour") val hour: Int,
    @SerialName("minute") val minute: Int,
    @SerialName("locationType") private val locationType: Int,
    @SerialName("weatherProvider") private val weatherProvider: Int,
    @SerialName("type") private val type: Int,
) : EntityModel {
    fun getLocationType(): LocationType = when (val locationType = LocationType.fromKey(locationType)) {
        is LocationType.CustomLocation -> locationType.copy(0, latitude, longitude, addressName)
        else -> locationType
    }

    fun getWeatherProvider(): WeatherDataProvider = WeatherDataProvider.fromKey(weatherProvider)

    fun getType(): DailyNotificationType = DailyNotificationType.fromKey(type)
}