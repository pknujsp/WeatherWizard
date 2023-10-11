package io.github.pknujsp.weatherwizard.core.model.notification

import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
class OngoingNotificationInfoEntity(
    val latitude: Double,
    val longitude: Double,
    val autoRefreshInterval: Long,
    private val weatherProvider: String,
    private val notificationIconType: String,
    private val createdDateTimeISO8601: String,
) : NotificationInfoEntity<OngoingNotificationInfoEntity>(serializer()) {

    override val notificationTypeId: Int = -1

    fun getCreatedDateTime() = ZonedDateTime.parse(createdDateTimeISO8601)

    fun getWeatherProvider(): WeatherDataProvider = WeatherDataProvider.fromKey(weatherProvider)

    fun getNotificationIconType(): NotificationIconType = NotificationIconType.valueOf(notificationIconType)
}