@file:OptIn(ExperimentalStdlibApi::class)

package de.joshuagleitze.gatling.simulationlog.parser

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.Charsets
import com.fleeksoft.charset.CodingErrorActionValue.REPORT
import com.fleeksoft.io.ByteBufferFactory
import org.antlr.v4.kotlinruntime.Parser
import org.antlr.v4.kotlinruntime.RecognitionException

fun isKnownCharset(coder: Byte) = coder.toCharset() != null

fun Byte.toCharset() = when (this) {
    0.toByte() -> Charsets.ISO_8859_1
    1.toByte() -> Charsets.UTF_16
    else -> null
}

fun Byte.toDecoder(): CharsetDecoder? = toCharset()?.let { ThrowingDecoders(it) }

fun ByteArray.decodeToString(decoder: CharsetDecoder): String = decoder.decode(ByteBufferFactory.wrap(this)).toString()

fun Parser.decodeString(bytes: ByteArray, decoder: CharsetDecoder): String {
    try {
        return bytes.decodeToString(decoder)
    } catch (_: Exception) {
        throw MalformedStringInputException(this, decoder.charset(), bytes)
    }
}

private object ThrowingDecoders {
    private val ISO_8859_1 = Charsets.ISO_8859_1.newDecoder().onMalformedInput(REPORT).onUnmappableCharacter(REPORT)
    private val UTF_16 = Charsets.UTF_16.newDecoder().onMalformedInput(REPORT).onUnmappableCharacter(REPORT)

    operator fun invoke(charset: Charset) = when (charset) {
        Charsets.ISO_8859_1 -> ISO_8859_1
        Charsets.UTF_16 -> UTF_16
        else -> error("No throwing decoder for $charset!")
    }
}


val bytesFormat = HexFormat {
    bytes {
        bytesPerGroup = 1
        groupSeparator = " "
    }
}

class MalformedStringInputException(parser: Parser, charset: Charset, input: ByteArray):RecognitionException(parser, parser.tokenStream, parser.context, "Failed to decode input string as $charset: ${input.toHexString(bytesFormat)}") {
    init {
        offendingToken = parser.currentToken
    }
}