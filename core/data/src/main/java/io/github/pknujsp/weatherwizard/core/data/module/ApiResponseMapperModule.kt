package io.github.pknujsp.weatherwizard.core.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.data.weather.kma.KmaResponseMapper
import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapper
import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapperManager
import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapperManagerImpl
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.KmaCurrentWeatherResponse
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.KmaDailyForecastResponse
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.KmaHourlyForecastResponse
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiResponseMapperModule {

    const val KMA_WEATHER_RESPONSE_MAPPER = "KmaWeatherResponseMapper"

    @Provides
    @Singleton
    @Named(KMA_WEATHER_RESPONSE_MAPPER)
    fun providesKmaResponseMapper(): WeatherResponseMapper<KmaCurrentWeatherResponse, KmaHourlyForecastResponse, KmaDailyForecastResponse, KmaCurrentWeatherResponse> =
        KmaResponseMapper()

    @Singleton
    @Provides
    fun providesWeatherResponseMapperManager(
        @Named(KMA_WEATHER_RESPONSE_MAPPER)
        kmaResponseMapper: WeatherResponseMapper<KmaCurrentWeatherResponse, KmaHourlyForecastResponse, KmaDailyForecastResponse, KmaCurrentWeatherResponse>
    ): WeatherResponseMapperManager = WeatherResponseMapperManagerImpl(kmaResponseMapper)
}