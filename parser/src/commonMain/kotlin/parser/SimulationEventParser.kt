package de.joshuagleitze.gatling.simulationlog.parser

import de.joshuagleitze.gatling.simulationlog.*

internal class SimulationEventParser(
    private val log: GatlingSimulationLogReader,
    private val runMetadata: SimulationRunMetadata
) : AutoCloseable by log {
    private val stringCache = mutableListOf<String>()

    fun readSimulationEvent(): SimulationEvent {
        val headerByte = log.readByte()
        return when (headerByte) {
            1.toByte() -> readResponseEvent()
            2.toByte() -> readUserEvent()
            3.toByte() -> readGroupEvent()
            4.toByte() -> readErrorEvent()
            else -> throw IllegalStateException("Unbekannter Event-Typ mit Header: $headerByte")
        }
    }

    // region parse private event types

    private fun readResponseEvent(): RequestFinishedEvent = RequestFinishedEvent(
        groups = readGroupHierarchy(),
        request = readCachedString(),
        startedAt = readTimestamp(),
        at = readTimestamp(),
        isOk = log.readBoolean(),
        message = readCachedString()
    )

    private fun readUserEvent(): SimulationEvent {
        val scenarioName = readScenarioName()
        val isStart = log.readBoolean()
        val timestamp = readTimestamp()

        return if (isStart) {
            UserStartedEvent(
                at = timestamp,
                scenario = scenarioName
            )
        } else {
            UserFinishedEvent(
                at = timestamp,
                scenario = scenarioName
            )
        }
    }

    private fun readGroupEvent(): GroupFinishedEvent = GroupFinishedEvent(
        groups = readGroupHierarchy(),
        startedAt = readTimestamp(),
        at = readTimestamp(),
        cumulatedResponseTime = log.readDuration(),
        isOk = log.readBoolean(),
    )

    private fun readErrorEvent(): ErrorEvent = ErrorEvent(
        message = readCachedString(),
        at = readTimestamp()
    )

    // endregion

    // region parse custom Gatling types

    private fun readScenarioName(): String {
        val scenarioIndex = log.readInt()
        return try {
            runMetadata.scenarioNames[scenarioIndex]
        } catch (_: IndexOutOfBoundsException) {
            error("Found invalid scenarion index <$scenarioIndex>! Expected value between 0 and ${runMetadata.scenarioNames.size - 1}.")
        }
    }

    private fun readGroupHierarchy(): List<String> = log.readListOf { readCachedString() }

    // endregion

    // region parse conventional data types

    private fun readTimestamp() = runMetadata.timestamp(afterStart = log.readDuration())

    private fun readCachedString(): String {
        val cacheIndex = log.readInt()
        return if (cacheIndex < 0) {
            try {
                stringCache[-cacheIndex - 1]
            } catch (_: IndexOutOfBoundsException) {
                // TODO better error handling
                error("Found invalid cache index <$cacheIndex>, expected a value betwen -1 and -${stringCache.size}.")
            }
        } else if (cacheIndex > 0) {
            val value = log.readString()
            // TODO better error handling
            require(cacheIndex == stringCache.size + 1) {
                if (cacheIndex <= stringCache.size) {
                    "Found invalid attempt to override string cache at index <$cacheIndex>. Existing value: <${stringCache[cacheIndex - 1]}>. Attempted new value: $value"
                } else {
                    "Found invalid next string cache index <$cacheIndex>, expected <${stringCache.size + 1}>."
                }
            }
            stringCache += value
            value
        } else {
            error("Found invalid cache index <$cacheIndex>")
        }
    }

    // endregion

    internal fun exhausted(): Boolean = log.exhausted()

}