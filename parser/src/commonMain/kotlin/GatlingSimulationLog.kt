package de.joshuagleitze.gatling.simulationlog

import de.joshuagleitze.gatling.simulationlog.parser.GatlingSimulationLogReader
import de.joshuagleitze.gatling.simulationlog.parser.RunRecordParser
import de.joshuagleitze.gatling.simulationlog.parser.SimulationEventParser
import kotlinx.io.Source

class GatlingSimulationLog private constructor(
    private val eventParser: SimulationEventParser,
    private val metadata: SimulationRunMetadata
) : SimulationRunMetadata by metadata, AutoCloseable by eventParser {
    val events: Sequence<SimulationEvent>
        get() = generateSequence {
            if (!eventParser.exhausted()) eventParser.readSimulationEvent() else null
        }

    companion object {
        fun open(logInput: Source): GatlingSimulationLog {
            val reader = GatlingSimulationLogReader(logInput)
            val runRecord = RunRecordParser(reader).readRunRecord()
            return GatlingSimulationLog(
                SimulationEventParser(reader, runRecord),
                runRecord,
            )
        }
    }

}


