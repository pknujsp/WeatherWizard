package io.github.pknujsp.everyweather.core.network.api.rainviewer

import io.github.pknujsp.everyweather.core.network.retrofit.onResult


internal class RainViewerDataSourceImpl(
    private val rainViewerNetworkApi: RainViewerNetworkApi
) : RainViewerDataSource {
    override suspend fun getJson(): Result<RainViewerResponse> {
        return rainViewerNetworkApi.getJson().onResult()
    }
}