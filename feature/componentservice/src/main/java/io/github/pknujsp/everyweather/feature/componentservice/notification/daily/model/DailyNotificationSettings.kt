package io.github.pknujsp.everyweather.feature.componentservice.notification.daily.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.pknujsp.everyweather.core.model.UiModel
import io.github.pknujsp.everyweather.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.everyweather.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider

class DailyNotificationSettings(
    type: DailyNotificationType, location: LocationTypeModel, hour: Int, minute: Int, weatherProvider: WeatherProvider
) : UiModel {
    var type by mutableStateOf(type)
    var location by mutableStateOf(location)
    var hour by mutableIntStateOf(hour)
    var minute by mutableIntStateOf(minute)
    var weatherDataProvider by mutableStateOf(weatherProvider)

    private val timeFormat = "%02d:%02d"
    val timeText: String
        get() = timeFormat.format(hour, minute)
}