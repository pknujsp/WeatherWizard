package io.github.pknujsp.weatherwizard.core.model

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import kotlinx.serialization.serializer
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream

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
}