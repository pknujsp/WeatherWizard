package io.github.pknujsp.weatherwizard.core.network.api.aqicn

import io.github.pknujsp.weatherwizard.core.network.retrofit.onResult
import javax.inject.Inject


class AqiCnDataSourceImpl @Inject constructor(
    private val aqiCnNetworkApi: AqiCnNetworkApi
) : AqiCnDataSource {
    override suspend fun getAqiCnData(latitude: Double, longitude: Double): Result<AqiCnResponse> {
        return aqiCnNetworkApi.getAqiCn(latitude, longitude).onResult()
    }
}