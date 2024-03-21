package io.github.pknujsp.everyweather.core.network.api.nominatim

import io.github.pknujsp.everyweather.core.network.api.nominatim.response.GeoCodeResponse
import io.github.pknujsp.everyweather.core.network.api.nominatim.response.ReverseGeoCodeResponse
import io.github.pknujsp.everyweather.core.network.retrofit.NetworkApiResult
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NominatimNetworkApi {
    // nominatim reverse geocode
    // https://nominatim.openstreetmap.org/reverse?format=geojson&lat=44.50155&lon=11.33989
    @GET("reverse")
    suspend fun reverseGeoCode(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("format") format: String = "geojson",
        @Query("zoom") zoom: Int = 14,
        @Header("Accept-Language") lang: String?,
    ): NetworkApiResult<ReverseGeoCodeResponse>

    // nominatim geocode
    // https://nominatim.openstreetmap.org/search?q=%EB%82%B4%EB%8F%99&format=geojson&addressdetails=1
    @GET("search")
    suspend fun geoCode(
        @Query("q") query: String,
        @Query("format") format: String = "geojson",
        @Query("addressdetails") addressDetails: Int = 1,
        @Header("Accept-Language") lang: String?,
    ): NetworkApiResult<GeoCodeResponse>
}
