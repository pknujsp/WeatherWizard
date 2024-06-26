package io.github.pknujsp.everyweather.core.model.nominatim

import io.github.pknujsp.everyweather.core.model.EntityModel

data class ReverseGeoCodeEntity(
    val displayName: String,
    val countryCode: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val county: String,
    val city: String,
    val province: String,
    val road: String,
    val quarter: String,
    val state: String,
    val suburb: String,
    val natural: String,
) : EntityModel {
    val simpleDisplayName: String =
        StringBuilder().apply {
            if (province.isNotEmpty()) append(" ").append(province)
            if (city.isNotEmpty()) append(" ").append(city)
            if (county.isNotEmpty()) append(" ").append(county)
            if (suburb.isNotEmpty()) append(" ").append(suburb)
            if (natural.isNotEmpty()) append(" ").append(natural)
            if (quarter.isNotEmpty()) append(" ").append(quarter)

            if (isEmpty()) append(displayName.replace(", $country", ""))
        }.toString().trim()
}
