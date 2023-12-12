package io.github.pknujsp.weatherwizard.feature.notification.ongoing

import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.toCalendar
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.model.OngoingNotificationRemoteViewUiState
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.model.OngoingNotificationRemoteViewUiModel

class OngoingNotificationUiModelMapper {
    fun map(model: OngoingNotificationRemoteViewUiState, units: CurrentUnits): OngoingNotificationRemoteViewUiModel {
        val entity = (model.state as WeatherResponseState.Success).entity

        return OngoingNotificationRemoteViewUiModel(model.notification.location.address,
            model.notification.notificationIconType,
            entity.toEntity<CurrentWeatherEntity>(),
            entity.toEntity<HourlyForecastEntity>(),
            DayNightCalculator(model.notification.location.latitude, model.notification.location.longitude),
            model.updatedTime.toCalendar(),
            units)
    }
}