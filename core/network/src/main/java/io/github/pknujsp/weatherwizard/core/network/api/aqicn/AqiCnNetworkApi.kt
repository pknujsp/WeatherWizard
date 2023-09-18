package io.github.pknujsp.weatherwizard.core.network.api.aqicn

import io.github.pknujsp.weatherwizard.core.model.BuildConfig
import io.github.pknujsp.weatherwizard.core.network.retrofit.NetworkApiResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface AqiCnNetworkApi {
    //https://api.waqi.info/feed/geo:35.235421;128.868227/?token=8538c6118653f6e4acbfd8ae5667bd07683a1cde
    @GET("feed/geo:{latitude};{longitude}/")
    suspend fun getAqiCn(
        @Path(value = "latitude", encoded = true) latitude: Double,
        @Path(value = "longitude", encoded = true) longitude: Double, @Query("token") token: String = BuildConfig.AQICN_KEY
    ): NetworkApiResult<AqiCnResponse>
}