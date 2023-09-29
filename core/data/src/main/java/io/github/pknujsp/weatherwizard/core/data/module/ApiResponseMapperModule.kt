package io.github.pknujsp.weatherwizard.core.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.data.weather.kma.KmaResponseMapper
import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapper
import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapperManager
import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapperManagerImpl
import io.github.pknujsp.weatherwizard.core.data.weather.metnorway.MetNorwayResponseMapper
import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaCurrentWeatherResponse
import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaDailyForecastResponse
import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaHourlyForecastResponse
import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaYesterdayWeatherResponse
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayCurrentWeatherResponse
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayDailyForecastResponse
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayHourlyForecastResponse
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiResponseMapperModule {

    private const val KMA_WEATHER_RESPONSE_MAPPER = "KmaWeatherResponseMapper"
    private const val METNORWAY_WEATHER_RESPONSE_MAPPER = "MetNorwayWeatherResponseMapper"

    @Provides
    @Singleton
    @Named(KMA_WEATHER_RESPONSE_MAPPER)
    fun providesKmaResponseMapper(): WeatherResponseMapper<KmaCurrentWeatherResponse, KmaHourlyForecastResponse,
            KmaDailyForecastResponse, KmaYesterdayWeatherResponse> = KmaResponseMapper()

    @Provides
    @Singleton
    @Named(METNORWAY_WEATHER_RESPONSE_MAPPER)
    fun providesMetNorwayResponseMapper(): WeatherResponseMapper<MetNorwayCurrentWeatherResponse, MetNorwayHourlyForecastResponse,
            MetNorwayDailyForecastResponse, KmaYesterdayWeatherResponse> = MetNorwayResponseMapper()

    @Singleton
    @Provides
    fun providesWeatherResponseMapperManager(
        @Named(KMA_WEATHER_RESPONSE_MAPPER)
        kmaResponseMapper: WeatherResponseMapper<KmaCurrentWeatherResponse, KmaHourlyForecastResponse, KmaDailyForecastResponse,
                KmaYesterdayWeatherResponse>,
        @Named(METNORWAY_WEATHER_RESPONSE_MAPPER)
        metNorwayResponseMapper: WeatherResponseMapper<MetNorwayCurrentWeatherResponse, MetNorwayHourlyForecastResponse,
                MetNorwayDailyForecastResponse, KmaYesterdayWeatherResponse>
    ): WeatherResponseMapperManager = WeatherResponseMapperManagerImpl(kmaResponseMapper, metNorwayResponseMapper)
}