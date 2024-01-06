package io.github.pknujsp.weatherwizard.core.network.api.nominatim.response


import io.github.pknujsp.weatherwizard.core.model.ApiResponseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeoCodeResponse(
    @SerialName("features") val features: List<Feature> = listOf(),
    @SerialName("licence") val licence: String = "", // Data © OpenStreetMap contributors, ODbL 1.0. http://osm.org/copyright
    @SerialName("type") val type: String = "" // FeatureCollection
) : ApiResponseModel {

    @Serializable
    data class Feature(
        @SerialName("bbox") val bbox: List<Double> = listOf(),
        @SerialName("geometry") val geometry: Geometry = Geometry(),
        @SerialName("properties") val properties: Properties = Properties(),
        @SerialName("type") val type: String = "" // Feature
    ) {
        @Serializable
        data class Geometry(
            @SerialName("coordinates") val coordinates: List<Double> = listOf(), @SerialName("type") val type: String = "" // Point
        )

        @Serializable
        data class Properties(
            @SerialName("address") val address: Address = Address(), @SerialName("addresstype") val addresstype: String = "", // village
            @SerialName("category") val category: String = "", // place
            @SerialName("display_name") val displayName: String = "", // 내동, 철산군, 평안북도, 조선민주주의인민공화국
            @SerialName("importance") val importance: Double = 0.0, // 0.27501
            @SerialName("name") val name: String = "", // 내동
            @SerialName("osm_id") val osmId: Long = 0, // 6740268469
            @SerialName("osm_type") val osmType: String = "", // node
            @SerialName("place_id") val placeId: Long = 0, // 227658872
            @SerialName("place_rank") val placeRank: Int = 0, // 19
            @SerialName("type") val type: String = "" // village
        ) {
            @Serializable
            data class Address(
                @SerialName("borough") val borough: String = "", // 서구
                @SerialName("city") val city: String = "", // 김해시
                @SerialName("city_district") val cityDistrict: String = "", // 오정동
                @SerialName("country") val country: String = "", // 대한민국
                @SerialName("country_code") val countryCode: String = "", // kp
                @SerialName("county") val county: String = "", // 철산군
                @SerialName("hamlet") val hamlet: String = "", // 내동
                @SerialName("ISO3166-2-lvl4") val iso3166: String = "", // KP-03
                @SerialName("postcode") val postCode: String = "", // 50952
                @SerialName("province") val province: String = "", // 경상남도
                @SerialName("quarter") val quarter: String = "", // 내동
                @SerialName("state") val state: String = "", // 평안북도
                @SerialName("suburb") val suburb: String = "", // 동인천동
                @SerialName("village") val village: String = "", // 내동
                @SerialName("road") val road: String = "", // 구지로
                @SerialName("natural") val natural: String = "" // 자연
            )
        }
    }
}