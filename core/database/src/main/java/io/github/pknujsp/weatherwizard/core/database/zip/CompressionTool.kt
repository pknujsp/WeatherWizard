package io.github.pknujsp.weatherwizard.core.database.zip

import net.jpountz.lz4.LZ4Compressor
import net.jpountz.lz4.LZ4Factory
import java.lang.ref.WeakReference


internal class CompressionToolImpl : CompressionTool {

    private val lz4Factory = LZ4Factory.fastestInstance()

    override fun compress(src: ByteArray): ByteArray? {
        return WeakReference(src).get()?.let { weakSrc ->
            val compressor: LZ4Compressor = lz4Factory.fastCompressor()
            val maxCompressedLength = compressor.maxCompressedLength(weakSrc.size)

            val compressed = ByteArray(maxCompressedLength)
            val compressedLength = compressor.compress(weakSrc, 0, weakSrc.size, compressed, 0, maxCompressedLength)
            compressed.copyOf(compressedLength)
        }
    }

    override fun deCompress(compressed: ByteArray): ByteArray? {
        return WeakReference(ByteArray(compressed.size * 4)).get()?.let { decomp ->
            val decompressor = lz4Factory.safeDecompressor()
            val decompressedLength = decompressor.decompress(compressed, decomp)
            decomp.copyOf(decompressedLength)
        }
    }

}

interface CompressionTool {
    fun compress(src: ByteArray): ByteArray?
    fun deCompress(compressed: ByteArray): ByteArray?
}