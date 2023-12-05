package io.github.pknujsp.weatherwizard.core.data.widget

import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class WidgetSettingsJsonEntity(
    @SerialName("latitude") private val latitude: Double,
    @SerialName("longitude") private val longitude: Double,
    @SerialName("address") private val address: String,
    @SerialName("country") private val country: String,
    @SerialName("locationType") private val locationType: Int,
    @SerialName("weatherProvider") private val weatherProvider: Int,
) : EntityModel {
    fun getLocation() = LocationTypeModel(
        locationType = LocationType.fromKey(locationType),
        latitude = latitude,
        longitude = longitude,
        address = address,
        country = country
    )

    fun getWeatherProvider() = WeatherProvider.fromKey(weatherProvider)
}