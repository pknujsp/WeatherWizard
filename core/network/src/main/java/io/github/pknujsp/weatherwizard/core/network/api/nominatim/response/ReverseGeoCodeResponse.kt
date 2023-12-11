package io.github.pknujsp.weatherwizard.core.network.api.nominatim.response


import io.github.pknujsp.weatherwizard.core.model.ApiResponseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReverseGeoCodeResponse(
    @SerialName("features") val features: List<Feature>,
    @SerialName("licence") val licence: String = "", // Data © OpenStreetMap contributors, ODbL 1.0. http://osm.org/copyright
    @SerialName("type") val type: String = "" // FeatureCollection
) : ApiResponseModel {
    @Serializable
    data class Feature(
        @SerialName("bbox") val bbox: List<Double>,
        @SerialName("geometry") val geometry: Geometry,
        @SerialName("properties") val properties: Properties,
        @SerialName("type") val type: String = "" // Feature
    ) {
        @Serializable
        data class Geometry(
            @SerialName("coordinates") val coordinates: List<Double> = listOf(),
            @SerialName("type") val type: String = "" // Point
        )

        @Serializable
        data class Properties(
            @SerialName("address") val address: Address,
            @SerialName("addresstype") val addresstype: String = "", // place
            @SerialName("category") val category: String = "", // place
            @SerialName("display_name")
            val displayName: String = "", // 71, Via Guglielmo Marconi, Marconi, Porto-Saragozza, Bologna, Emilia-Romagna, 40122, 이탈리아
            @SerialName("importance") val importance: Double = 0.0, // 9.99999999995449e-06
            @SerialName("name") val name: String = "",
            @SerialName("osm_id") val osmId: String = "", // 1704756187
            @SerialName("osm_type") val osmType: String = "", // node
            @SerialName("place_id") val placeId: Int = 0, // 97614718
            @SerialName("place_rank") val placeRank: Int = 0, // 30
            @SerialName("type") val type: String = "" // house
        ) {
            @Serializable
            data class Address(
                @SerialName("city") val city: String = "", // Bologna
                @SerialName("country") val country: String = "", // 이탈리아
                @SerialName("country_code") val countryCode: String = "", // it
                @SerialName("county") val county: String = "", // Bologna
                @SerialName("house_number") val houseNumber: String = "", // 71
                @SerialName("ISO3166-2-lvl4") val iso3166Lv14: String = "", // IT-45
                @SerialName("ISO3166-2-lvl6") val iso3166Lv16: String = "", // IT-BO
                @SerialName("postcode") val postCode: String = "", // 40122
                @SerialName("road") val road: String = "", // Via Guglielmo Marconi
                @SerialName("state") val state: String = "", // Emilia-Romagna
                @SerialName("suburb") val suburb: String = "", // Marconi
                @SerialName("province") val province: String = "", // Bologna
                @SerialName("quarter") val quarter: String = "" // Porto-Saragozza
            )
        }
    }
}