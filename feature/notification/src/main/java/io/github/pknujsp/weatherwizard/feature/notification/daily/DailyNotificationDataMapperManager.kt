package io.github.pknujsp.weatherwizard.feature.notification.daily

import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.feature.WeatherDataMapper
import io.github.pknujsp.weatherwizard.feature.DataMapperManager
import io.github.pknujsp.weatherwizard.feature.notification.daily.type.forecast.DailyNotificationForecastDataMapper

object DailyNotificationDataMapperManager : DataMapperManager<DailyNotificationType> {
    override fun getMapperByType(type: DailyNotificationType): WeatherDataMapper = when (type) {
        DailyNotificationType.FORECAST -> DailyNotificationForecastDataMapper()
        DailyNotificationType.AIR_QUALITY -> DailyNotificationForecastDataMapper()
        DailyNotificationType.CURRENT -> DailyNotificationForecastDataMapper()
        else -> DailyNotificationForecastDataMapper()
    }

}