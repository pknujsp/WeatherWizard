package io.github.pknujsp.weatherwizard.core.network.api.rainviewer

import io.github.pknujsp.weatherwizard.core.network.datasource.rainviewer.RainViewerResponse

interface RainViewerDataSource {
    suspend fun getJson(): Result<RainViewerResponse>
}