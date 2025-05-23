package de.joshuagleitze.gatling.simulationlog.parser.tokens

import org.antlr.v4.kotlinruntime.CharStream
import org.antlr.v4.kotlinruntime.Token
import org.antlr.v4.kotlinruntime.Token.Companion.DEFAULT_CHANNEL
import org.antlr.v4.kotlinruntime.TokenSource

sealed interface ByteToken : Token {
    val value: UByte

    override val stopIndex: Int
        get() = startIndex

    override val charPositionInLine: Int
        get() = startIndex

    override val line: Int
        get() = 1

    override val tokenIndex: Int
        get() = startIndex

    override val channel: Int get() = DEFAULT_CHANNEL

    override val inputStream: CharStream?
        get() = null

    override val text: String?
        get() = null

    override val tokenSource: TokenSource?
        get() = null

    class Value(
        override val value: UByte,
        override val startIndex: Int
    ) : ByteToken {
        override val type: Int
            get() = 1
    }

    class EOF(override val startIndex: Int) : ByteToken {
        override val value: UByte
            get() = error("Cannot get the value of an EOF token!")

        override val type: Int
            get() = Token.EOF
    }
}

