package io.github.pknujsp.weatherwizard.core.model.notification

import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.time.ZonedDateTime

@Serializable
class OngoingNotificationInfoEntity(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val addressName: String = "address",
    private val autoRefreshInterval: Long = 0,
    private val weatherProvider: String = WeatherDataProvider.default.key,
    private val notificationIconType: String = NotificationIconType.TEMPERATURE.name,
    private val createdDateTimeISO8601: String = ZonedDateTime.now().toString(),
) : NotificationEntityModel {
    fun getCreatedDateTime() = ZonedDateTime.parse(createdDateTimeISO8601)

    fun getWeatherProvider(): WeatherDataProvider = WeatherDataProvider.fromKey(weatherProvider)

    fun getNotificationIconType(): NotificationIconType = NotificationIconType.valueOf(notificationIconType)

    fun getAutoRefreshInterval(): RefreshInterval = RefreshInterval.entries.first { it.interval == autoRefreshInterval }
}

class NotificationInfoEntityParser(
    val json: Json
) {

    @OptIn(InternalSerializationApi::class)
    inline fun <reified E : NotificationEntityModel> parse(entity: String): E {
        return json.decodeFromString(E::class.serializer(), entity) as E
    }

    @OptIn(InternalSerializationApi::class)
    inline fun <reified E : NotificationEntityModel> parse(entity: E): String {
        return json.encodeToString(E::class.serializer(), entity)
    }
}