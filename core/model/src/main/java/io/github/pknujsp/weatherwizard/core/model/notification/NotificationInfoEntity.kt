package io.github.pknujsp.weatherwizard.core.model.notification

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
abstract class NotificationInfoEntity<E : NotificationInfoEntity<E>>(
    private val serializer: KSerializer<E>
) : EntityModel {
    abstract val notificationTypeId: Int

    fun parse(entity: String, json: Json): E {
        return json.decodeFromString(serializer, entity)
    }

    fun parse(entity: E, json: Json): String {
        return json.encodeToString(serializer, entity)
    }
}