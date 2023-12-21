package io.github.pknujsp.weatherwizard.core.network.api.rainviewer

import io.github.pknujsp.weatherwizard.core.network.retrofit.onResult


internal class RainViewerDataSourceImpl(
    private val rainViewerNetworkApi: RainViewerNetworkApi
) : RainViewerDataSource {
    override suspend fun getJson(): Result<RainViewerResponse> {
        return rainViewerNetworkApi.getJson().onResult()
    }
}