package io.github.pknujsp.weatherwizard.core.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

class JsonParser(
    val json: Json
) {

    @OptIn(InternalSerializationApi::class)
    inline fun <reified E : EntityModel> parse(entity: String): E {
        return json.decodeFromString(E::class.serializer(), entity)
    }

    @OptIn(InternalSerializationApi::class)
    inline fun <reified E : EntityModel> parse(entity: E): String {
        return json.encodeToString(E::class.serializer(), entity)
    }
}