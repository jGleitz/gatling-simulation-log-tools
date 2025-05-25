package de.joshuagleitze.gatling.simulationlog

import kotlin.time.Duration
import kotlin.time.Instant

interface SimulationRunMetadata {
    val gatlingVersion: String
    val simulationClassName: String
    val runStart: Instant
    val runDescription: String
    val scenarioNames: List<String>
    val assertions: List<ByteArray>

    fun timestamp(afterStart: Duration): Timestamp = Timestamp(runStart, afterStart)
}