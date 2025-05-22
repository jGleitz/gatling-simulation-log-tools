package de.joshuagleitze.gatling.simulationlog.parser

import de.joshuagleitze.gatling.simulationlog.parser.rules.value
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import org.antlr.v4.kotlinruntime.UnbufferedTokenStream
import tokens.ByteTokenSource

fun main(args: Array<String>) {
    val input = SystemFileSystem.source(Path(args[0])).buffered()
    val parser = GatlingSimulationLog(UnbufferedTokenStream(ByteTokenSource(input)))
    val runRecord = parser.runRecord()
    println("gatling version length: <${runRecord.gatlingVersion!!.length!!.value}>")
    println("gatling version: <${runRecord.gatlingVersion!!.value}>")
    println("simulation length: <${runRecord.simulationClassName!!.length!!.value}>")
    println("simulation: <${parser.runRecord().simulationClassName!!.value}>")
}