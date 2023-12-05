package io.github.pknujsp.weatherwizard.core.data.notification.daily.model

import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class DailyNotificationSettingsJsonEntity(
    @SerialName("latitude") private val latitude: Double,
    @SerialName("longitude") private val longitude: Double,
    @SerialName("address") private val address: String,
    @SerialName("country") private val country: String,
    @SerialName("hour") val hour: Int,
    @SerialName("minute") val minute: Int,
    @SerialName("locationType") private val locationType: Int,
    @SerialName("weatherProvider") private val weatherProvider: Int,
    @SerialName("type") private val type: Int,
) : EntityModel {
    fun getLocation() = LocationTypeModel(
        locationType = LocationType.fromKey(locationType),
        latitude = latitude,
        longitude = longitude,
        address = address,
        country = country
    )

    fun getWeatherProvider(): WeatherProvider = WeatherProvider.fromKey(weatherProvider)

    fun getType(): DailyNotificationType = DailyNotificationType.fromKey(type)
}