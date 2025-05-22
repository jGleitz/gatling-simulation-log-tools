package de.joshuagleitze.gatling.simulationlog.parser.rules

import de.joshuagleitze.gatling.simulationlog.parser.GatlingSimulationLog

val GatlingSimulationLog.StringContext.value: String get() = this.bytes!!.value!!.decodeToString()