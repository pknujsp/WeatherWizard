package io.github.pknujsp.weatherwizard.core.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.data.RepositoryCacheManager
import io.github.pknujsp.weatherwizard.core.data.GlobalRepositoryCacheManager
import io.github.pknujsp.weatherwizard.core.data.GlobalRepositoryCacheManagerImpl
import io.github.pknujsp.weatherwizard.core.data.RepositoryInitializer
import io.github.pknujsp.weatherwizard.core.data.aqicn.AirQualityRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.rainviewer.RadarTilesRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.searchhistory.SearchHistoryRepository
import io.github.pknujsp.weatherwizard.core.data.searchhistory.SearchHistoryRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepositoryImpl
import io.github.pknujsp.weatherwizard.core.database.searchhistory.SearchHistoryLocalDataSource
import io.github.pknujsp.weatherwizard.core.model.EntityModel
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScopeRepositoryModule {

    const val WEATHER_REPOSITORY = "WEATHER_REPOSITORY"
    const val AIR_QUALITY_REPOSITORY = "AIR_QUALITY_REPOSITORY"
    const val RAIN_VIEWER_REPOSITORY = "RAIN_VIEWER_REPOSITORY"
    const val SETTINGS_REPOSITORY = "SETTINGS_REPOSITORY"

    @Provides
    fun providesSearchHistoryRepository(searchHistoryLocalDataSource: SearchHistoryLocalDataSource): SearchHistoryRepository =
        SearchHistoryRepositoryImpl(searchHistoryLocalDataSource)


    @Provides
    @Named(WEATHER_REPOSITORY)
    internal fun providesWeatherRepositoryInitializer(weatherDataRepositoryImpl: WeatherDataRepositoryImpl): RepositoryCacheManager<*, *> =
        weatherDataRepositoryImpl

    @Provides
    @Named(AIR_QUALITY_REPOSITORY)
    internal fun providesAirQualityRepositoryInitializer(airQualityRepositoryImpl: AirQualityRepositoryImpl): RepositoryCacheManager<*, *> =
        airQualityRepositoryImpl

    @Provides
    @Named(RAIN_VIEWER_REPOSITORY)
    internal fun providesRainViewerRepositoryInitializer(radarTilesRepositoryImpl: RadarTilesRepositoryImpl): RepositoryCacheManager<*, *> =
        radarTilesRepositoryImpl

    @Provides
    @Named(SETTINGS_REPOSITORY)
    internal fun providesSettingsRepositoryInitializer(settingsRepositoryImpl: SettingsRepositoryImpl): RepositoryInitializer =
        settingsRepositoryImpl

    @Provides
    @Singleton
    fun providesRepositoryInitializer(
        @Named(WEATHER_REPOSITORY) weatherRepository: RepositoryCacheManager<*, *>,
        @Named(AIR_QUALITY_REPOSITORY) airQualityRepository: RepositoryCacheManager<*, *>,
        @Named(RAIN_VIEWER_REPOSITORY) rainViewerRepository: RepositoryCacheManager<*, *>
    ): GlobalRepositoryCacheManager = GlobalRepositoryCacheManagerImpl(weatherRepository, airQualityRepository, rainViewerRepository)

}