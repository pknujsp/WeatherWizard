package io.github.pknujsp.everyweather.core.data.module

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.data.ai.SummaryTextRepository
import io.github.pknujsp.everyweather.core.data.ai.SummaryTextRepositoryImpl
import io.github.pknujsp.everyweather.core.data.aqicn.AirQualityRepository
import io.github.pknujsp.everyweather.core.data.aqicn.AirQualityRepositoryImpl
import io.github.pknujsp.everyweather.core.data.cache.CacheManagerImpl
import io.github.pknujsp.everyweather.core.data.favorite.FavoriteAreaListRepository
import io.github.pknujsp.everyweather.core.data.favorite.FavoriteAreaListRepositoryImpl
import io.github.pknujsp.everyweather.core.data.favorite.TargetLocationRepository
import io.github.pknujsp.everyweather.core.data.favorite.TargetLocationRepositoryImpl
import io.github.pknujsp.everyweather.core.data.mapper.JsonParser
import io.github.pknujsp.everyweather.core.data.nominatim.NominatimRepository
import io.github.pknujsp.everyweather.core.data.nominatim.NominatimRepositoryImpl
import io.github.pknujsp.everyweather.core.data.notification.daily.DailyNotificationRepository
import io.github.pknujsp.everyweather.core.data.notification.daily.DailyNotificationRepositoryImpl
import io.github.pknujsp.everyweather.core.data.notification.ongoing.OngoingNotificationRepository
import io.github.pknujsp.everyweather.core.data.notification.ongoing.OngoingNotificationRepositoryImpl
import io.github.pknujsp.everyweather.core.data.rainviewer.RadarTilesRepository
import io.github.pknujsp.everyweather.core.data.rainviewer.RadarTilesRepositoryImpl
import io.github.pknujsp.everyweather.core.data.searchhistory.SearchHistoryRepository
import io.github.pknujsp.everyweather.core.data.searchhistory.SearchHistoryRepositoryImpl
import io.github.pknujsp.everyweather.core.data.settings.SettingsRepository
import io.github.pknujsp.everyweather.core.data.settings.SettingsRepositoryImpl
import io.github.pknujsp.everyweather.core.data.weather.WeatherDataRepository
import io.github.pknujsp.everyweather.core.data.weather.WeatherDataRepositoryImpl
import io.github.pknujsp.everyweather.core.data.weather.mapper.WeatherResponseMapperManager
import io.github.pknujsp.everyweather.core.data.weather.model.CachedWeatherModel
import io.github.pknujsp.everyweather.core.data.weather.request.WeatherApiRequestManager
import io.github.pknujsp.everyweather.core.data.widget.WidgetRepository
import io.github.pknujsp.everyweather.core.data.widget.WidgetRepositoryImpl
import io.github.pknujsp.everyweather.core.database.AppDataStore
import io.github.pknujsp.everyweather.core.database.favoritearea.FavoriteAreaListDataSource
import io.github.pknujsp.everyweather.core.database.notification.daily.DailyNotificationLocalDataSource
import io.github.pknujsp.everyweather.core.database.notification.ongoing.OngoingNotificationLocalDataSource
import io.github.pknujsp.everyweather.core.database.searchhistory.SearchHistoryLocalDataSource
import io.github.pknujsp.everyweather.core.database.widget.WidgetLocalDataSource
import io.github.pknujsp.everyweather.core.model.ApiResponseModel
import io.github.pknujsp.everyweather.core.model.BuildConfig
import io.github.pknujsp.everyweather.core.model.airquality.AirQualityEntity
import io.github.pknujsp.everyweather.core.model.rainviewer.RadarTiles
import io.github.pknujsp.everyweather.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.everyweather.core.network.api.aqicn.AqiCnDataSource
import io.github.pknujsp.everyweather.core.network.api.nominatim.NominatimDataSource
import io.github.pknujsp.everyweather.core.network.api.rainviewer.RainViewerDataSource
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    companion object {
        @Provides
        @Singleton
        @JvmStatic
        internal fun providesWeatherRepositoryImpl(
            weatherResponseMapperManager: WeatherResponseMapperManager<@JvmSuppressWildcards WeatherEntityModel>,
            weatherApiRequestManager: WeatherApiRequestManager<@JvmSuppressWildcards ApiResponseModel>,
            @CoDispatcher(CoDispatcherType.IO) dispatcher: CoroutineDispatcher,
        ): WeatherDataRepositoryImpl {
            val cacheManagerImpl = CacheManagerImpl<Int, CachedWeatherModel>(dispatcher = dispatcher)
            return WeatherDataRepositoryImpl(
                weatherResponseMapperManager,
                weatherApiRequestManager,
                cacheManagerImpl,
                cacheManagerImpl,
            )
        }

        @Provides
        @Singleton
        @JvmStatic
        fun providesNominatimRepository(nominatimDataSource: NominatimDataSource): NominatimRepository =
            NominatimRepositoryImpl(nominatimDataSource)

        @Provides
        @Singleton
        @JvmStatic
        internal fun providesRadartilesRepositoryImpl(
            rainViewerDataSource: RainViewerDataSource, @CoDispatcher(CoDispatcherType.IO) dispatcher: CoroutineDispatcher
        ): RadarTilesRepositoryImpl {
            val cacheManagerImpl = CacheManagerImpl<Long, RadarTiles>(cacheMaxSize = 1, dispatcher = dispatcher)
            return RadarTilesRepositoryImpl(rainViewerDataSource, cacheManagerImpl, cacheManagerImpl)
        }

        @Provides
        @Singleton
        @JvmStatic
        internal fun providesAirQualityRepositoryImpl(
            aqiCnDataSource: AqiCnDataSource, @CoDispatcher(CoDispatcherType.IO) dispatcher: CoroutineDispatcher
        ): AirQualityRepositoryImpl {
            val cacheManagerImpl = CacheManagerImpl<Int, AirQualityEntity>(cacheMaxSize = 4, dispatcher = dispatcher)
            return AirQualityRepositoryImpl(aqiCnDataSource, cacheManagerImpl, cacheManagerImpl)
        }

        @Provides
        @JvmStatic
        @Singleton
        fun providesFavoriteAreaRepository(favoriteAreaListDataSource: FavoriteAreaListDataSource): FavoriteAreaListRepository =
            FavoriteAreaListRepositoryImpl(favoriteAreaListDataSource)

        @Singleton
        @JvmStatic
        @Provides
        fun providesTargetAreaRepository(appDataStore: AppDataStore): TargetLocationRepository = TargetLocationRepositoryImpl(appDataStore)

        @Singleton
        @JvmStatic
        @Provides
        fun providesSettingsRepositoryImpl(appDataStore: AppDataStore): SettingsRepositoryImpl = SettingsRepositoryImpl(appDataStore)

        @Singleton
        @JvmStatic
        @Provides
        fun providesDailyNotificationRepository(
            dataSource: DailyNotificationLocalDataSource, jsonParser: JsonParser
        ): DailyNotificationRepository = DailyNotificationRepositoryImpl(dataSource, jsonParser)

        @Singleton
        @JvmStatic
        @Provides
        fun providesOngoingNotificationRepository(
            dataSource: OngoingNotificationLocalDataSource, jsonParser: JsonParser
        ): OngoingNotificationRepository = OngoingNotificationRepositoryImpl(dataSource, jsonParser)

        @Singleton
        @JvmStatic
        @Provides
        fun providesWidgetRepository(
            widgetLocalDataSource: WidgetLocalDataSource, jsonParser: JsonParser
        ): WidgetRepository = WidgetRepositoryImpl(widgetLocalDataSource, jsonParser)

        @Singleton
        @JvmStatic
        @Provides
        internal fun providesSummaryTextRepositoryImpl(
            @CoDispatcher(CoDispatcherType.IO) dispatcher: CoroutineDispatcher
        ): SummaryTextRepositoryImpl {
            val cacheManagerImpl = CacheManagerImpl<Int, List<GenerateContentResponse>>(
                cacheMaxSize = 5,
                dispatcher = dispatcher,
            )
            return SummaryTextRepositoryImpl(GenerativeModel("gemini-pro",
                BuildConfig.GOOGLE_AI_STUDIO_KEY,
                generationConfig {
                    temperature = 0.5f
                    topK = 20
                    topP = 0.95f
                },
                safetySettings = listOf(
                    SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.NONE),
                    SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.NONE),
                    SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.NONE),
                    SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.NONE),
                )), cacheManagerImpl, cacheManagerImpl)
        }


        @Singleton
        @JvmStatic
        @Provides
        fun providesSearchHistoryRepository(searchHistoryLocalDataSource: SearchHistoryLocalDataSource): SearchHistoryRepository =
            SearchHistoryRepositoryImpl(searchHistoryLocalDataSource)
    }


    @Binds
    internal abstract fun providesWeatherRepository(
        weatherDataRepositoryImpl: WeatherDataRepositoryImpl
    ): WeatherDataRepository


    @Binds
    internal abstract fun providesRadartilesRepository(radarTilesRepositoryImpl: RadarTilesRepositoryImpl): RadarTilesRepository


    @Binds
    internal abstract fun providesAirQualityRepository(
        airQualityRepositoryImpl: AirQualityRepositoryImpl
    ): AirQualityRepository


    @Binds
    internal abstract fun providesSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository


    @Binds
    internal abstract fun providesSummaryTextRepository(
        summaryTextRepositoryImpl: SummaryTextRepositoryImpl
    ): SummaryTextRepository

}