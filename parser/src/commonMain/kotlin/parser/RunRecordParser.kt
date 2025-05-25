package de.joshuagleitze.gatling.simulationlog.parser

import de.joshuagleitze.gatling.simulationlog.RunRecord

internal class RunRecordParser(private val log: GatlingSimulationLogReader) : AutoCloseable by log {
    internal fun readRunRecord(): RunRecord {
        log.requireHeader(0)
        return RunRecord(
            gatlingVersion = log.readString(),
            simulationClassName = log.readString(),
            runStart = log.readInstant(),
            runDescription = log.readString(),
            scenarioNames = log.readListOf { readString() },
            assertions = log.readListOf { readByteArray() }
        )
    }
}