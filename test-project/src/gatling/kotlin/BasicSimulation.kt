package de.joshuagleitze.gatling.simulationlog.example

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*
import io.gatling.javaapi.core.*
import io.gatling.javaapi.http.*

class BasicSimulation : Simulation() {
    private val httpProtocol: HttpProtocolBuilder = http
        .baseUrl("https://api-ecomm.gatling.io")
        .acceptHeader("application/json")
        .userAgentHeader(
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36"
        )

    private val scenario: ScenarioBuilder = scenario("Scenario")
        .exec(http("Session").get("/session"))

    private val assertion: Assertion = global().failedRequests().count().lt(1L)

    // Define injection profile and execute the test
    init {
        setUp(
            scenario.injectOpen(atOnceUsers(20))
        ).assertions(assertion).protocols(httpProtocol)
    }
}