package tokens

import de.joshuagleitze.gatling.simulationlog.parser.tokens.ByteToken
import kotlinx.io.EOFException
import kotlinx.io.Source
import kotlinx.io.readUByte
import org.antlr.v4.kotlinruntime.CharStream
import org.antlr.v4.kotlinruntime.CommonTokenFactory
import org.antlr.v4.kotlinruntime.Token
import org.antlr.v4.kotlinruntime.TokenFactory
import org.antlr.v4.kotlinruntime.TokenSource

class ByteTokenSource(private val input: Source) : TokenSource {
    private var readBytes: Int = 0
    override val charPositionInLine: Int
        get() = readBytes
    override val inputStream: CharStream?
        get() = null
    override val line: Int
        get() = 1
    override val sourceName: String
        get() = input.toString()
    override var tokenFactory: TokenFactory<*> = Factory

    override fun nextToken(): Token {
        val currentPosition = readBytes
        readBytes += 1
        return try {
            ByteToken.Value(input.readUByte(), currentPosition)
        } catch (_: EOFException) {
            ByteToken.EOF(currentPosition)
        }
    }

    object Factory : TokenFactory<ByteToken> {
        override fun create(
            type: Int,
            text: String
        ): ByteToken = when (type) {
            Token.EOF -> ByteToken.EOF(0)
            else -> ByteToken.Artificial(text, type, -1)
        }

        override fun create(
            source: Pair<TokenSource?, CharStream?>,
            type: Int,
            text: String?,
            channel: Int,
            start: Int,
            stop: Int,
            line: Int,
            charPositionInLine: Int
        ): ByteToken = when (type) {
            Token.EOF -> ByteToken.EOF(start)
            else -> ByteToken.Artificial(text ?: "", type, start)
        }

    }
}