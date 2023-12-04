package io.github.pknujsp.weatherwizard.feature.notification.daily.type.forecast

import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.feature.WeatherDataMapper
import io.github.pknujsp.weatherwizard.feature.notification.daily.model.forecast.DailyNotificationForecastUiModel

class DailyNotificationForecastDataMapper : WeatherDataMapper {
    override fun map(response: WeatherResponseState): WeatherDataMapper.MapperResult {
        return when (response) {
            is WeatherResponseState.Success -> {

                DailyNotificationForecastUiModel
            }

            else -> WeatherDataMapper.MapperResult.Failure
        }
    }

}