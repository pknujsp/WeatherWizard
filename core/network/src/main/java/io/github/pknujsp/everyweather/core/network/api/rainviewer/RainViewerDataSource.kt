package io.github.pknujsp.everyweather.core.network.api.rainviewer

interface RainViewerDataSource {
    suspend fun getJson(): Result<RainViewerResponse>
}
