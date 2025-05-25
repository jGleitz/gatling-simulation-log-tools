@file:OptIn(ExperimentalTime::class)

package de.joshuagleitze.gatling.simulationlog.parser

import de.joshuagleitze.gatling.simulationlog.parser.events.RequestFinishedEvent
import de.joshuagleitze.gatling.simulationlog.parser.events.SimulationEvent
import de.joshuagleitze.gatling.simulationlog.parser.events.UserFinishedEvent
import de.joshuagleitze.gatling.simulationlog.parser.events.UserStartedEvent
import kotlinx.io.Source
import org.antlr.v4.kotlinruntime.ConsoleErrorListener
import org.antlr.v4.kotlinruntime.DiagnosticErrorListener
import org.antlr.v4.kotlinruntime.UnbufferedTokenStream
import tokens.ByteTokenSource
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class GatlingSimulationLog private constructor(
    private val inputCloseable: AutoCloseable,
    private val parser: GatlingSimulationLogParser,
    val simulationClassName: String,
    val simulationStartedAt: Instant,
    val gatlingVersion: String,
    val scenarios: List<String>
) : AutoCloseable {
    private val stringCache = mutableListOf<String>()
    private val eventListener = EventListener()

    init {
        parser.addParseListener(eventListener)
    }

    val events: Sequence<SimulationEvent>
        get() = generateSequence {
            eventListener.event == null
            parser.event()
            eventListener.event!!
        }

    internal fun timestamp(msSinceStart: Int) = Timestamp(msSinceStart.milliseconds)

    override fun close() {
        inputCloseable.close()
    }

    fun toString(ctx: GatlingSimulationLogParser.CachedStringContext): String {
        val cacheIndex = ctx.cacheIndex!!.value!!
        return if (cacheIndex > 0) {
            check(stringCache.size == cacheIndex - 1) { "unexpected cache index <${cacheIndex}>, expected <${stringCache.size + 1}!" }
            val value = ctx.value!!.value!!
            stringCache.add(value)
            value
        } else {
            stringCache[cacheIndex - 1]
        }
    }

    fun toGroupList(ctx: GatlingSimulationLogParser.GroupHierarchyContext) =
        ctx.groups!!.map { toString(it) }

    inner class Timestamp(val sinceSimulationStart: Duration) {
        val wallTime: Instant get() = simulationStartedAt + sinceSimulationStart
    }

    private inner class EventListener : GatlingSimulationLogParserBaseListener() {
        var event: SimulationEvent? = null

        override fun exitUserEvent(ctx: GatlingSimulationLogParser.UserEventContext) {
            val at = timestamp(ctx.simulationMs!!.value!!)
            val scenarioName = scenarios[ctx.scenarioIndex!!.value!!]
            event = if (ctx.isStart!!.value!!)
                UserStartedEvent(at, scenarioName)
            else UserFinishedEvent(at, scenarioName)
        }

        override fun exitResponseEvent(ctx: GatlingSimulationLogParser.ResponseEventContext) {
            event = RequestFinishedEvent(
                timestamp(ctx.endSimulationMs!!.value!!),
                timestamp(ctx.startSimulationMs!!.value!!),
                toGroupList(ctx.groups!!),
                toString(ctx.requestName!!),
                ctx.isOk!!.value!!,
                toString(ctx.message!!))
        }
    }

    companion object {
        fun open(logInput: Source): GatlingSimulationLog {
            val parser = GatlingSimulationLogParser(UnbufferedTokenStream(ByteTokenSource(logInput)))
            // TODO:
            parser.addErrorListener(DiagnosticErrorListener())
            parser.addErrorListener(ConsoleErrorListener())
            val runRecord = parser.runRecord()
            return GatlingSimulationLog(
                logInput,
                parser,
                runRecord.simulationClassName!!.value!!,
                Instant.fromEpochMilliseconds(runRecord.startEpochMillis!!.value!!),
                runRecord.gatlingVersion!!.value!!,
                runRecord.scenarioNames!!.map { it.value!! }
            )
        }
    }

}


