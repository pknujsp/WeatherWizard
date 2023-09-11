package io.github.pknujsp.weatherwizard.core.network.api.rainviewer

interface RainViewerDataSource {
    suspend fun getJson(): Result<RainViewerResponse>
}