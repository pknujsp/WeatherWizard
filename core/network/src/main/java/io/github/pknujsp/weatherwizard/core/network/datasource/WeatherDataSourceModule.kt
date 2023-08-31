package io.github.pknujsp.weatherwizard.core.network.datasource

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaNetworkApi
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.KmaDataSource
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.KmaDataSourceImpl
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.parser.KmaHtmlParser
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherDataSourceModule {
    @Provides
    @Singleton
    fun providesKmaDataSource(
        kmaNetworkApi: KmaNetworkApi,
        kmaHtmlParser: KmaHtmlParser,
    ): KmaDataSource = KmaDataSourceImpl(kmaNetworkApi, kmaHtmlParser)
}