package io.github.pknujsp.weatherwizard.core.data.widget

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class WidgetSettingsJsonEntity(
    @SerialName("latitude") private val latitude: Double,
    @SerialName("longitude") private val longitude: Double,
    @SerialName("addressName") private val addressName: String,
    @SerialName("locationTypeKey") private val locationTypeKey: Int,
    @SerialName("weatherProviderKey") private val weatherProviderKey: Int,
) : EntityModel {
    fun getLocationType() = when (val locationType = LocationType.fromKey(locationTypeKey)) {
        is LocationType.CurrentLocation -> locationType
        is LocationType.CustomLocation -> locationType.copy(address = addressName, latitude = latitude, longitude = longitude)
    }

    fun getWeatherProvider() = WeatherProvider.fromKey(weatherProviderKey)
}