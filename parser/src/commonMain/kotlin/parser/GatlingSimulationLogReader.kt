package de.joshuagleitze.gatling.simulationlog.parser

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.Charsets
import com.fleeksoft.charset.CodingErrorActionValue
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.ByteBufferFactory
import kotlinx.io.Source
import kotlinx.io.readByteArray
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

internal class GatlingSimulationLogReader(private val input: Source) : AutoCloseable by input {
    private var bytesRead: UInt = 0u

    // region parse basic Gatling structures

    internal fun requireHeader(expectedValue: Byte) {
        val headerByte = readByte()
        // TODO better error handling
        require(headerByte == expectedValue) { "Expected header <${expectedValue.toHex()}> but found <${headerByte.toHex()}>" }
    }

    // endregion

    // region parse conventional data types

    internal fun readString(): String {
        val length = readInt()
        if (length == 0) return ""

        val content = readByteBuffer(length)
        val decoder = ThrowingDecoders(readCharset())

        try {
            return decoder.decode(content).toString()
        } catch (e: Exception) {
            // TODO better error handling
            error("Could not decode string using ${decoder.charset()}: ${e.message}")
        }
    }

    internal fun readDuration(): Duration = readInt().milliseconds

    internal fun readInstant(): Instant = Instant.Companion.fromEpochMilliseconds(readLong())

    internal fun readByteArray() = readByteArray(length = readInt())

    internal fun readByteBuffer(length: Int): ByteBuffer = ByteBufferFactory.wrap(readByteArray(length))

    internal fun readByteArray(length: Int) = input.readByteArray(length).also { bytesRead += length.toUInt() }

    internal fun readCharset() = when (readByte()) {
        0.toByte() -> Charsets.ISO_8859_1
        1.toByte() -> Charsets.UTF_16
        // TODO better error handling
        else -> error("Unknown charset value: ${readByte().toHex()}")
    }

    // endregion

    // region parse primitive data types

    internal inline fun <T> readListOf(itemReader: GatlingSimulationLogReader.() -> T): List<T> =
        List(readInt()) { itemReader() }

    internal fun readLong(): Long = input.readLong().also { bytesRead += Long.SIZE_BYTES.toUInt() }

    internal fun readInt(): Int = input.readInt().also { bytesRead += Int.SIZE_BYTES.toUInt() }

    internal fun readBoolean(): Boolean {
        val byteValue = readByte()
        require(byteValue == 0.toByte() || byteValue == 1.toByte()) { "Not a boolean value: ${byteValue.toHex()}" }
        return byteValue == 1.toByte()
    }

    internal fun readByte(): Byte = input.readByte().also { bytesRead += Byte.SIZE_BYTES.toUInt() }

    // endregion

    // region non-parsing concerns

    private fun Byte.toHex() = toHexString(HexFormat.Companion.UpperCase)

    private object ThrowingDecoders {
        private val ISO_8859_1 = Charsets.ISO_8859_1.newDecoder().onMalformedInput(CodingErrorActionValue.REPORT).onUnmappableCharacter(
            CodingErrorActionValue.REPORT
        )
        private val UTF_16 = Charsets.UTF_16.newDecoder().onMalformedInput(CodingErrorActionValue.REPORT).onUnmappableCharacter(
            CodingErrorActionValue.REPORT
        )

        operator fun invoke(charset: Charset) = when (charset) {
            Charsets.ISO_8859_1 -> ISO_8859_1
            Charsets.UTF_16 -> UTF_16
            else -> error("No throwing decoder for $charset!")
        }
    }

    internal fun exhausted() = input.exhausted()

    // endregion
}