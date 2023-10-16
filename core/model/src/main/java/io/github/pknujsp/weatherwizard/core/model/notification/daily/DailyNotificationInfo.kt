package io.github.pknujsp.weatherwizard.core.model.notification.daily

import androidx.compose.runtime.Stable
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationUiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider

@Stable
class DailyNotificationInfo(
    override var latitude: Double,
    override var longitude: Double,
    override var addressName: String,
    override var locationType: LocationType,
    override var weatherProvider: WeatherDataProvider,
    val enabled: Boolean,
    val id: Long,
    var type: DailyNotificationType,
    var hour: Int,
    var minute: Int,
) : NotificationUiModel {
    val time: String get() = String.format("%02d:%02d", hour, minute)
}