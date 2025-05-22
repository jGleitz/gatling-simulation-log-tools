package tokens

import de.joshuagleitze.gatling.simulationlog.parser.tokens.ByteToken
import kotlinx.io.EOFException
import kotlinx.io.Source
import kotlinx.io.readUByte
import org.antlr.v4.kotlinruntime.CharStream
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
    override var tokenFactory: TokenFactory<*>
        get() = throw UnsupportedOperationException()
        set(_) {
            throw UnsupportedOperationException()
        }

    override fun nextToken(): Token {
        val currentPosition = readBytes
        readBytes += 1
        return try {
            ByteToken.Value(input.readUByte(), currentPosition)
        } catch (_: EOFException) {
            ByteToken.EOF(currentPosition)
        }
    }
}