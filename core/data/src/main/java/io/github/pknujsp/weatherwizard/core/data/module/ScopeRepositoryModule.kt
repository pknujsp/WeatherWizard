package io.github.pknujsp.weatherwizard.core.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.data.RepositoryCacheManager
import io.github.pknujsp.weatherwizard.core.data.GlobalRepositoryCacheManager
import io.github.pknujsp.weatherwizard.core.data.GlobalRepositoryCacheManagerImpl
import io.github.pknujsp.weatherwizard.core.data.aqicn.AirQualityRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.rainviewer.RadarTilesRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.searchhistory.SearchHistoryRepository
import io.github.pknujsp.weatherwizard.core.data.searchhistory.SearchHistoryRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepositoryImpl
import io.github.pknujsp.weatherwizard.core.database.searchhistory.SearchHistoryLocalDataSource
import io.github.pknujsp.weatherwizard.core.model.EntityModel
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScopeRepositoryModule {

    private const val WEATHER_REPOSITORY = "WEATHER_REPOSITORY"
    private const val AIR_QUALITY_REPOSITORY = "AIR_QUALITY_REPOSITORY"
    private const val RAIN_VIEWER_REPOSITORY = "RAIN_VIEWER_REPOSITORY"

    @Provides
    fun providesSearchHistoryRepository(searchHistoryLocalDataSource: SearchHistoryLocalDataSource): SearchHistoryRepository =
        SearchHistoryRepositoryImpl(searchHistoryLocalDataSource)


    @Provides
    @Named(WEATHER_REPOSITORY)
    fun providesWeatherRepositoryInitializer(weatherDataRepositoryImpl: WeatherDataRepositoryImpl): RepositoryCacheManager<*> =
        weatherDataRepositoryImpl

    @Provides
    @Named(AIR_QUALITY_REPOSITORY)
    fun providesAirQualityRepositoryInitializer(airQualityRepositoryImpl: AirQualityRepositoryImpl): RepositoryCacheManager<*> =
        airQualityRepositoryImpl

    @Provides
    @Named(RAIN_VIEWER_REPOSITORY)
    fun providesRainViewerRepositoryInitializer(radarTilesRepositoryImpl: RadarTilesRepositoryImpl): RepositoryCacheManager<*> =
        radarTilesRepositoryImpl

    @Provides
    @Singleton
    fun providesRepositoryInitializer(
        @Named(WEATHER_REPOSITORY) weatherRepository: RepositoryCacheManager<*>,
        @Named(AIR_QUALITY_REPOSITORY) airQualityRepository: RepositoryCacheManager<*>,
        @Named(RAIN_VIEWER_REPOSITORY) rainViewerRepository: RepositoryCacheManager<*>
    ): GlobalRepositoryCacheManager = GlobalRepositoryCacheManagerImpl(weatherRepository, airQualityRepository, rainViewerRepository)

}