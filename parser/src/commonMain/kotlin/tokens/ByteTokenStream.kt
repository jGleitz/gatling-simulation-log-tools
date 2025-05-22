package de.joshuagleitze.gatling.simulationlog.parser.tokens

import com.strumenta.antlrkotlin.runtime.System
import kotlinx.io.EOFException
import kotlinx.io.Source
import kotlinx.io.readUByte
import org.antlr.v4.kotlinruntime.RuleContext
import org.antlr.v4.kotlinruntime.Token
import org.antlr.v4.kotlinruntime.TokenSource
import org.antlr.v4.kotlinruntime.TokenStream
import org.antlr.v4.kotlinruntime.misc.Interval
import kotlin.math.min

class ByteTokenStream(private val input: Source, initialBufferSize: Int = 2 shl 4) : TokenStream {
    /**
     * A moving window buffer of the data being scanned. While there's a marker,
     * we keep adding to buffer. Otherwise, [consume] resets, so
     * we start filling at index `0` again.
     */
    private var tokenBuffer: Array<ByteToken?> = arrayOfNulls(initialBufferSize)

    /**
     * The number of tokens currently in [tokenBuffer].
     *
     * This is not the buffer capacity, that's `tokens.length`.
     */
    private var bufferedTokenCount: Int = 0

    /**
     * `0..bufferedTokenCount-1` index into [tokenBuffer] of next token.
     *
     * The `LT(1)` token is `tokens[nextTokenIndex]`.
     * If `nextTokenIndex == bufferedTokenCount`, we are out of buffered tokens.
     */
    private var nextTokenIndex: Int = 0

    /**
     * Position of the last byte we read from [input].
     */
    private var inputPosition: Int = 0

    /**
     * Count up with [mark] and down with [release].
     *
     * When we [release] the last mark, [validMarkerCount] reaches `0` and we reset the buffer.
     * Copy `tokenBuffer[nextTokenIndex]..tokenBuffer[bufferedTokenCount-1]` to `tokenBuffer[0]..tokenBuffer[(bufferedTokenCount-1)-nextTokenIndex]`.
     */
    private var validMarkerCount: Int = 0

    /**
     * This is the `LT(-1)` token for the current position.
     */
    protected var previousToken: ByteToken? = null

    /**
     * When [validMarkerCount] `> 0`, this is the `LT(-1)` token for the
     * first token in [bufferedTokenCount]. Otherwise, this is `null`.
     */
    protected var previousTokenForBufferStart: ByteToken? = null

    private val nextTokenUnsafe: ByteToken get() = tokenBuffer[nextTokenIndex]!!

    // we exploit the fact that the [inputPosition] is also the token index since every input byte is one token
    private val bufferStartIndex: Int
        get() = nextTokenUnsafe.startIndex - nextTokenIndex

    init {
        // Prime the pump
        fill(1)
    }

    override operator fun get(index: Int): Token {
        val bufferStartIndex = bufferStartIndex

        if (index < bufferStartIndex || index >= bufferStartIndex + bufferedTokenCount) {
            throw IndexOutOfBoundsException("get($index) outside buffer: $bufferStartIndex..${bufferStartIndex + bufferedTokenCount}")
        }

        return tokenBuffer[index - bufferStartIndex]!!
    }

    override fun LT(k: Int): ByteToken {
        if (k == -1) {
            return previousToken!!
        }

        sync(k)
        val index = nextTokenIndex + k - 1

        if (index < 0) {
            throw IndexOutOfBoundsException("LT($k) gives negative index")
        }

        if (index >= bufferedTokenCount) {
            check(bufferedTokenCount > 0 && tokenBuffer[bufferedTokenCount - 1]!!.type == Token.EOF)
            return tokenBuffer[bufferedTokenCount - 1]!!
        }

        return tokenBuffer[index]!!
    }

    override val sourceName: String
        get() = TODO("Not yet implemented")

    override fun LA(i: Int): Int =
        LT(i).type

    override fun consume() {
        check(LT(1) !is ByteToken.EOF) { "cannot consume EOF!" }

        // Track last token for LT(-1)
        previousToken = tokenBuffer[nextTokenIndex]

        // If we're at last token and no markers, opportunity to flush buffer
        if (nextTokenIndex == bufferedTokenCount - 1 && validMarkerCount == 0) {
            bufferedTokenCount = 0
            nextTokenIndex = -1 // nextTokenIndex += 1 will leave this at 0
            previousTokenForBufferStart = previousToken
        }

        nextTokenIndex += 1
        sync(1)
    }

    /**
     * Make sure we have `need` elements from current position [nextTokenIndex].
     * Last valid [nextTokenIndex] index is `tokenBuffer.length - 1`. `nextTokenIndex + need - 1` is the tokens
     * index `need` elements ahead.
     * If we need `1` element, `(nextTokenIndex + 1 - 1) == nextTokenIndex` must be less than `tokens.length`.
     */
    private fun sync(want: Int) {
        // How many more elements we need?
        val need = nextTokenIndex + want - 1 - bufferedTokenCount + 1

        if (need > 0) {
            fill(need)
        }
    }

    /**
     * Add [n] elements to the buffer.
     *
     * Returns the number of tokens actually added to the buffer.
     * If the return value is less than [n], then `EOF` was reached
     * before [n] tokens could be added.
     */
    private fun fill(n: Int): Int {
        if (bufferedTokenCount > 0 && tokenBuffer[bufferedTokenCount - 1] is ByteToken.EOF) {
            return 0
        }
        for (i in 0..<n) {
            if (add(nextToken()) is ByteToken.EOF) {
                return i
            }
        }

        return n
    }

    /**
     * Creates the next [ByteToken] for the next byte read from [input], or [ByteToken.EOF] if [input] has no more tokens.
     */
    private fun nextToken(): ByteToken {
        inputPosition += 1
        return try {
            ByteToken.Value(input.readUByte(), inputPosition)
        } catch (_: EOFException) {
            ByteToken.EOF(inputPosition)
        }
    }

    private fun add(token: ByteToken): ByteToken {
        if (bufferedTokenCount >= tokenBuffer.size) {
            tokenBuffer = tokenBuffer.copyOf(tokenBuffer.size * 2)
        }

        tokenBuffer[bufferedTokenCount++] = token
        return token
    }

    /**
     * Return a marker that we can release later.
     *
     * The specific marker value used for this class allows for some level of
     * protection against misuse where [seek] is called on a mark or [release]
     * is called in the wrong order.
     */
    override fun mark(): Int {
        if (validMarkerCount == 0) {
            previousTokenForBufferStart = previousToken
        }

        val mark = -validMarkerCount - 1
        validMarkerCount += 1
        return mark
    }

    override fun release(marker: Int) {
        check(marker == -validMarkerCount) { "release() called with an invalid marker: $marker" }

        validMarkerCount -= 1

        // Can we release the buffer?
        if (validMarkerCount == 0) {
            if (nextTokenIndex > 0) {
                // Copy tokens[nextTokenIndex]..tokens[bufferedTokenCount-1] to tokens[0]..tokens[(bufferedTokenCount-1)-nextTokenIndex], reset pointers
                System.arraycopy(tokenBuffer, nextTokenIndex, tokenBuffer, 0, bufferedTokenCount - nextTokenIndex)
                bufferedTokenCount -= nextTokenIndex
                nextTokenIndex = 0
            }

            previousTokenForBufferStart = previousToken
        }
    }

    override fun index(): Int = nextTokenUnsafe.startIndex

    override fun seek(index: Int) {
        val currentTokenIndex = index()
        var tempIndex = index

        if (tempIndex == currentTokenIndex) {
            return
        }

        val bufferStartIndex = currentTokenIndex - nextTokenIndex

        if (tempIndex > currentTokenIndex) {
            sync(tempIndex - currentTokenIndex)
            tempIndex = min(tempIndex, bufferStartIndex + bufferedTokenCount - 1)
        }

        val i = tempIndex - bufferStartIndex

        if (i < 0) {
            throw IllegalArgumentException("cannot seek to negative index $tempIndex")
        } else if (i >= bufferedTokenCount) {
            throw UnsupportedOperationException(
                "seek to index outside buffer: $tempIndex not in $bufferStartIndex..${bufferStartIndex + bufferedTokenCount}"
            )
        }

        nextTokenIndex = i
        previousToken = if (nextTokenIndex == 0) {
            previousTokenForBufferStart
        } else {
            tokenBuffer[nextTokenIndex - 1]
        }
    }

    override fun size(): Int = throw UnsupportedOperationException()

    override val text: String
        get() = throw UnsupportedOperationException()

    override fun getText(ctx: RuleContext) = throw UnsupportedOperationException()
    override fun getText(
        start: Token?,
        stop: Token?
    ): String? = throw UnsupportedOperationException()

    override fun getText(interval: Interval): String = throw UnsupportedOperationException()
    override val tokenSource: TokenSource
        get() = throw UnsupportedOperationException()
}