package de.joshuagleitze.gatling.simulationlog.parser

import de.joshuagleitze.gatling.simulationlog.parser.model.SimulationLog
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import org.antlr.v4.kotlinruntime.ConsoleErrorListener
import org.antlr.v4.kotlinruntime.DiagnosticErrorListener
import org.antlr.v4.kotlinruntime.UnbufferedTokenStream
import tokens.ByteTokenSource

fun main(args: Array<String>) {
    val input = SystemFileSystem.source(Path(args[0])).buffered()
    val parser = GatlingSimulationLogParser(UnbufferedTokenStream(ByteTokenSource(input)))
    parser.addErrorListener(DiagnosticErrorListener())
    parser.addErrorListener(ConsoleErrorListener())
    val runRecord = parser.runRecord()
    println(runRecord.toStringTree(parser))
    val simulationLog = SimulationLog(
        runRecord.simulationClassName!!.value!!,
        runRecord.gatlingVersion!!.value!!,
        runRecord.scenarioNames!!.map { it.value!! })
    println(simulationLog)
}