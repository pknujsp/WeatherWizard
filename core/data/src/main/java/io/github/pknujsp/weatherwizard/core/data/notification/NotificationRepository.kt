package io.github.pknujsp.weatherwizard.core.data.notification

import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedUnit
import kotlinx.coroutines.flow.StateFlow

interface NotificationRepository {


    suspend fun getNotificationInfo(): TemperatureUnit
    suspend fun setNotificationInfo(unit: TemperatureUnit)



}