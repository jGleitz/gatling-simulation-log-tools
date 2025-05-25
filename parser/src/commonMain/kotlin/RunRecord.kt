package de.joshuagleitze.gatling.simulationlog

import kotlin.time.Instant

internal data class RunRecord(
    override val gatlingVersion: String,
    override val simulationClassName: String,
    override val runStart: Instant,
    override val runDescription: String,
    override val scenarioNames: List<String>,
    override val assertions: List<ByteArray>
) : SimulationRunMetadata