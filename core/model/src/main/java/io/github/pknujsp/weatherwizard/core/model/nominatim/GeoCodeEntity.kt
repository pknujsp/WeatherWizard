package io.github.pknujsp.weatherwizard.core.model.nominatim

import io.github.pknujsp.weatherwizard.core.model.EntityModel

data class GeoCodeEntity(
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
) : EntityModel {

    // 순서 : country > province > city > county > suburb > quarter > road
    val simpleDisplayName: String = StringBuilder().apply {
        if (province.isNotEmpty()) append(" ").append(province)
        if (city.isNotEmpty()) append(" ").append(city)
        if (county.isNotEmpty()) append(" ").append(county)
        if (suburb.isNotEmpty()) append(" ").append(suburb)
        if (quarter.isNotEmpty()) append(" ").append(quarter)
    }.toString().trim()
}