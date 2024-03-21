package io.github.pknujsp.everyweather.core.database.zip

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

internal class CompressionToolImpl : CompressionTool {
    override fun compress(src: ByteArray): ByteArray {
        return ByteArrayOutputStream().use { byteArrayOutputStream ->
            GZIPOutputStream(byteArrayOutputStream).buffered().use { it.write(src) }
            byteArrayOutputStream.toByteArray()
        }
    }

    override fun deCompress(compressed: ByteArray): ByteArray {
        return ByteArrayInputStream(compressed).use { byteStream ->
            GZIPInputStream(byteStream).buffered().use { it.readBytes() }
        }
    }
}

interface CompressionTool {
    fun compress(src: ByteArray): ByteArray

    fun deCompress(compressed: ByteArray): ByteArray
}
