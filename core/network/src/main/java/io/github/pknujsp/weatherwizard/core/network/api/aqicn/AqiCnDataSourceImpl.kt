package io.github.pknujsp.weatherwizard.core.network.api.aqicn

import io.github.pknujsp.weatherwizard.core.network.retrofit.onResult
import javax.inject.Inject


class AqiCnDataSourceImpl @Inject constructor(
    private val aqiCnNetworkApi: AqiCnNetworkApi
) : AqiCnDataSource {

    private val ok = "ok"
    override suspend fun getAqiCnData(latitude: Double, longitude: Double): Result<AqiCnResponse> {
        return aqiCnNetworkApi.getAqiCn(latitude, longitude).onResult().fold(
            onSuccess = { if (it.status == ok) Result.success(it) else Result.failure(Throwable("대기질 정보 응답 실패")) },
            onFailure = { Result.failure(it) }
        )
    }
}