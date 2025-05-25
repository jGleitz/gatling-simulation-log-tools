package de.joshuagleitze.gatling.simulationlog

import kotlin.time.Duration

sealed interface SimulationEvent {
    val at: Timestamp
}

data class UserStartedEvent(override val at: Timestamp, val scenario: String) : SimulationEvent
data class UserFinishedEvent(override val at: Timestamp, val scenario: String) : SimulationEvent
data class RequestFinishedEvent(
    override val at: Timestamp,
    val startedAt: Timestamp,
    val groups: List<String>,
    val request: String,
    val isOk: Boolean,
    val message: String
) : SimulationEvent

data class GroupFinishedEvent(
    override val at: Timestamp,
    val startedAt: Timestamp,
    val groups: List<String>,
    val isOk: Boolean,
    val cumulatedResponseTime: Duration
) : SimulationEvent

data class ErrorEvent(override val at: Timestamp, val message: String) : SimulationEvent


