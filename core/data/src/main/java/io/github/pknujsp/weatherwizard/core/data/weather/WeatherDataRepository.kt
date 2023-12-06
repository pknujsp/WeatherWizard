package io.github.pknujsp.weatherwizard.core.data.weather

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeatherEntity
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.time.toKotlinDuration

interface WeatherDataRepository {
    suspend fun getWeatherData(
        majorWeatherEntityType: MajorWeatherEntityType,
        latitude: Double,
        longitude: Double,
        weatherProvider: WeatherProvider,
        requestId: Long,
        bypassCache: Boolean = true
    ): Result<EntityModel>
}

interface WeatherDataRepositoryInitializer {
    suspend fun initialize()
}