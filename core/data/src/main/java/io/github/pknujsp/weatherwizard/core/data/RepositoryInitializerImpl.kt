package io.github.pknujsp.weatherwizard.core.data

import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepositoryInitializer
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

class RepositoryInitializerImpl @Inject constructor(
    private val weatherDataRepositoryInitializer: WeatherDataRepositoryInitializer,
) : RepositoryInitializer {
    override suspend fun initialize() {
        supervisorScope {
            launch {
                weatherDataRepositoryInitializer.initialize()
            }
        }
    }
}