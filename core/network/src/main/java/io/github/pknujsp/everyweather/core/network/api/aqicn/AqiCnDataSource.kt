package io.github.pknujsp.everyweather.core.network.api.aqicn

interface AqiCnDataSource {
    suspend fun getAqiCnData(latitude: Double, longitude: Double): Result<AqiCnResponse>
}