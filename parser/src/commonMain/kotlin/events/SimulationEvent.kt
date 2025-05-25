package de.joshuagleitze.gatling.simulationlog.parser.events

import de.joshuagleitze.gatling.simulationlog.parser.GatlingSimulationLog
import kotlin.time.Duration

sealed interface SimulationEvent {
    val at: GatlingSimulationLog.Timestamp
}

data class UserStartedEvent(override val at: GatlingSimulationLog.Timestamp, val scenario: String) : SimulationEvent
data class UserFinishedEvent(override val at: GatlingSimulationLog.Timestamp, val scenario: String) : SimulationEvent
data class RequestFinishedEvent(override val at: GatlingSimulationLog.Timestamp, val startedAt: GatlingSimulationLog.Timestamp, val groups: List<String>, val request: String, val isOk: Boolean, val message: String) : SimulationEvent
data class GroupFinishedEvent(override val at: GatlingSimulationLog.Timestamp, val startedAt: GatlingSimulationLog.Timestamp, val groups: List<String>, val isOk: Boolean, val cumulatedResponseTime: Durationn) : SimulationEvent
data class ErrorEvent(override val at: GatlingSimulationLog.Timestamp, val message: String) : SimulationEvent