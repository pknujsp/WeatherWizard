package io.github.pknujsp.weatherwizard.core.model.notification

import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.time.ZonedDateTime

@Serializable
class OngoingNotificationInfoEntity(
    val latitude: Double,
    val longitude: Double,
    val autoRefreshInterval: Long,
    private val weatherProvider: String,
    private val notificationIconType: String,
    private val createdDateTimeISO8601: String,
) : NotificationEntityModel {
    fun getCreatedDateTime() = ZonedDateTime.parse(createdDateTimeISO8601)

    fun getWeatherProvider(): WeatherDataProvider = WeatherDataProvider.fromKey(weatherProvider)

    fun getNotificationIconType(): NotificationIconType = NotificationIconType.valueOf(notificationIconType)

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