package io.github.pknujsp.weatherwizard.core.network.api.rainviewer

import io.github.pknujsp.weatherwizard.core.network.datasource.rainviewer.RainViewerResponse
import io.github.pknujsp.weatherwizard.core.network.retrofit.onResult
import javax.inject.Inject


class RainViewerDataSourceImpl @Inject constructor(
    private val rainViewerNetworkApi: RainViewerNetworkApi
) : RainViewerDataSource {
    override suspend fun getJson(): Result<RainViewerResponse> {
        return rainViewerNetworkApi.getJson().onResult()
    }
}