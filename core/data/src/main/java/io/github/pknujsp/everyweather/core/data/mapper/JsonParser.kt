package io.github.pknujsp.everyweather.core.data.mapper

import io.github.pknujsp.everyweather.core.model.Model
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import kotlinx.serialization.serializer
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class JsonParser(
    val json: Json
) {

    @OptIn(InternalSerializationApi::class)
    inline fun <reified E : Model> parse(entity: String): E {
        return json.decodeFromString(E::class.serializer(), entity)
    }

    @OptIn(InternalSerializationApi::class)
    inline fun <reified E : Model> parse(entity: E): String {
        return json.encodeToString(E::class.serializer(), entity)
    }

    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    inline fun <reified E : Model> parseToByteArray(entity: E): ByteArray {
        return ByteArrayOutputStream().use {
            json.encodeToStream(E::class.serializer(), entity, it)
            it.toByteArray()
        }
    }

    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    inline fun <reified E : Model> parse(byteArray: ByteArray): E {
        return ByteArrayInputStream(byteArray).use {
            json.decodeFromStream(E::class.serializer(), it)
        }
    }
}