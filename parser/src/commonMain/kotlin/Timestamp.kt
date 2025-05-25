package de.joshuagleitze.gatling.simulationlog

import kotlin.time.Duration
import kotlin.time.Instant

data class Timestamp(private val runStart: Instant, val sinceRunStart: Duration) {
    val wallTime: Instant get() = runStart + sinceRunStart

    override fun toString() = sinceRunStart.toString()

    operator fun minus(other: Timestamp): Duration =
        sinceRunStart - requireSameStart(other).sinceRunStart

    private fun requireSameStart(other: Timestamp): Timestamp {
        require(other.runStart == runStart) { "Found different run start <${other.runStart}>, expected <$runStart>." }
        return other
    }
}