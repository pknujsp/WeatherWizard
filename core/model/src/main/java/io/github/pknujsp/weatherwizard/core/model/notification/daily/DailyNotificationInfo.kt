package io.github.pknujsp.weatherwizard.core.model.notification.daily

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationUiModel
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider

@Stable
data class DailyNotificationInfo(
    override var latitude: Double,
    override var longitude: Double,
    override var addressName: String,
    override var locationType: LocationType,
    override var weatherProvider: WeatherDataProvider,
    val enabled: Boolean,
    var id: Long,
    var type: DailyNotificationType,
    var hour: Int,
    var minute: Int,
) : NotificationUiModel {

    var onSaved: Boolean by mutableStateOf(false)
    val time: String get() = String.format("%02d:%02d", hour, minute)
}