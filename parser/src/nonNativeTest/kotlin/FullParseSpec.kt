import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.toContainExactly
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toHaveSize
import ch.tutteli.atrium.api.verbs.expect
import de.joshuagleitze.gatling.simulationlog.GatlingSimulationLog
import de.joshuagleitze.gatling.simulationlog.RequestFinishedEvent
import de.joshuagleitze.gatling.simulationlog.UserFinishedEvent
import de.joshuagleitze.gatling.simulationlog.UserStartedEvent
import io.kotest.core.spec.style.DescribeSpec
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

class FullParseSpec : DescribeSpec({
    describe("parsing full simulation logs") {
        it("parses") {
            val log = GatlingSimulationLog.open(
                SystemFileSystem.source(Path("src/nonNativeTest/resources/basic.simulation.log")).buffered()
            )
            expect(log) {
                feature({ f(it::gatlingVersion) }).toEqual("3.14.3")
                feature({ f(it::simulationClassName) }).toEqual("de.joshuagleitze.gatling.simulationlog.example.BasicSimulation")
                feature({ f(it::runStart) }).toEqual(Instant.parse("2025-05-25T20:54:44.487Z"))
                feature({ f(it::runDescription) }).toEqual("")
                feature({ f(it::scenarioNames) }).toContainExactly("Scenario")
                feature({ f(it::assertions) }).toHaveSize(1)
                feature({ f(it::events) }).feature({ f(it::toList) }).toContainExactly(
                    UserStartedEvent(at = log.timestamp(530.milliseconds), scenario = "Scenario"),
                    UserStartedEvent(at = log.timestamp(530.milliseconds), scenario = "Scenario"),
                    UserStartedEvent(at = log.timestamp(531.milliseconds), scenario = "Scenario"),
                    UserStartedEvent(at = log.timestamp(530.milliseconds), scenario = "Scenario"),
                    UserStartedEvent(at = log.timestamp(531.milliseconds), scenario = "Scenario"),
                    UserStartedEvent(at = log.timestamp(531.milliseconds), scenario = "Scenario"),
                    UserStartedEvent(at = log.timestamp(531.milliseconds), scenario = "Scenario"),
                    UserStartedEvent(at = log.timestamp(530.milliseconds), scenario = "Scenario"),
                    UserStartedEvent(at = log.timestamp(531.milliseconds), scenario = "Scenario"),
                    UserStartedEvent(at = log.timestamp(531.milliseconds), scenario = "Scenario"),
                    UserStartedEvent(at = log.timestamp(530.milliseconds), scenario = "Scenario"),
                    UserStartedEvent(at = log.timestamp(531.milliseconds), scenario = "Scenario"),
                    UserStartedEvent(at = log.timestamp(531.milliseconds), scenario = "Scenario"),
                    UserStartedEvent(at = log.timestamp(531.milliseconds), scenario = "Scenario"),
                    UserStartedEvent(at = log.timestamp(531.milliseconds), scenario = "Scenario"),
                    UserStartedEvent(at = log.timestamp(531.milliseconds), scenario = "Scenario"),
                    UserStartedEvent(at = log.timestamp(530.milliseconds), scenario = "Scenario"),
                    UserStartedEvent(at = log.timestamp(531.milliseconds), scenario = "Scenario"),
                    UserStartedEvent(at = log.timestamp(531.milliseconds), scenario = "Scenario"),
                    UserStartedEvent(at = log.timestamp(531.milliseconds), scenario = "Scenario"),
                    RequestFinishedEvent(
                        at = log.timestamp(2_655.milliseconds),
                        startedAt = log.timestamp(557.milliseconds),
                        groups = emptyList(),
                        request = "Session",
                        isOk = true,
                        message = ""
                    ),
                    RequestFinishedEvent(
                        at = log.timestamp(2_655.milliseconds),
                        startedAt = log.timestamp(558.milliseconds),
                        groups = emptyList(),
                        request = "Session",
                        isOk = true,
                        message = ""
                    ),
                    UserFinishedEvent(at = log.timestamp(2_667.milliseconds), scenario = "Scenario"),
                    UserFinishedEvent(at = log.timestamp(2_667.milliseconds), scenario = "Scenario"),
                    RequestFinishedEvent(
                        at = log.timestamp(2_698.milliseconds),
                        startedAt = log.timestamp(557.milliseconds),
                        groups = emptyList(),
                        request = "Session",
                        isOk = true,
                        message = ""
                    ),
                    RequestFinishedEvent(
                        at = log.timestamp(2_698.milliseconds),
                        startedAt = log.timestamp(558.milliseconds),
                        groups = emptyList(),
                        request = "Session",
                        isOk = true,
                        message = ""
                    ),
                    UserFinishedEvent(at = log.timestamp(2_700.milliseconds), scenario = "Scenario"),
                    UserFinishedEvent(at = log.timestamp(2_700.milliseconds), scenario = "Scenario"),
                    RequestFinishedEvent(
                        at = log.timestamp(2_718.milliseconds),
                        startedAt = log.timestamp(558.milliseconds),
                        groups = emptyList(),
                        request = "Session",
                        isOk = true,
                        message = ""
                    ),
                    UserFinishedEvent(at = log.timestamp(2_719.milliseconds), scenario = "Scenario"),
                    RequestFinishedEvent(
                        at = log.timestamp(2_739.milliseconds),
                        startedAt = log.timestamp(557.milliseconds),
                        groups = emptyList(),
                        request = "Session",
                        isOk = true,
                        message = ""
                    ),
                    UserFinishedEvent(at = log.timestamp(2_740.milliseconds), scenario = "Scenario"),
                    RequestFinishedEvent(
                        at = log.timestamp(2_752.milliseconds),
                        startedAt = log.timestamp(557.milliseconds),
                        groups = emptyList(),
                        request = "Session",
                        isOk = true,
                        message = ""
                    ),
                    UserFinishedEvent(at = log.timestamp(2_753.milliseconds), scenario = "Scenario"),
                    RequestFinishedEvent(
                        at = log.timestamp(2_769.milliseconds),
                        startedAt = log.timestamp(558.milliseconds),
                        groups = emptyList(),
                        request = "Session",
                        isOk = true,
                        message = ""
                    ),
                    UserFinishedEvent(at = log.timestamp(2_769.milliseconds), scenario = "Scenario"),
                    RequestFinishedEvent(
                        at = log.timestamp(2_773.milliseconds),
                        startedAt = log.timestamp(558.milliseconds),
                        groups = emptyList(),
                        request = "Session",
                        isOk = true,
                        message = ""
                    ),
                    UserFinishedEvent(at = log.timestamp(2_774.milliseconds), scenario = "Scenario"),
                    RequestFinishedEvent(
                        at = log.timestamp(2_788.milliseconds),
                        startedAt = log.timestamp(557.milliseconds),
                        groups = emptyList(),
                        request = "Session",
                        isOk = true,
                        message = ""
                    ),
                    UserFinishedEvent(at = log.timestamp(2_789.milliseconds), scenario = "Scenario"),
                    RequestFinishedEvent(
                        at = log.timestamp(2_793.milliseconds),
                        startedAt = log.timestamp(558.milliseconds),
                        groups = emptyList(),
                        request = "Session",
                        isOk = true,
                        message = ""
                    ),
                    UserFinishedEvent(at = log.timestamp(2_793.milliseconds), scenario = "Scenario"),
                    RequestFinishedEvent(
                        at = log.timestamp(2_796.milliseconds),
                        startedAt = log.timestamp(558.milliseconds),
                        groups = emptyList(),
                        request = "Session",
                        isOk = true,
                        message = ""
                    ),
                    UserFinishedEvent(at = log.timestamp(2_797.milliseconds), scenario = "Scenario"),
                    RequestFinishedEvent(
                        at = log.timestamp(2_807.milliseconds),
                        startedAt = log.timestamp(558.milliseconds),
                        groups = emptyList(),
                        request = "Session",
                        isOk = true,
                        message = ""
                    ),
                    UserFinishedEvent(at = log.timestamp(2_807.milliseconds), scenario = "Scenario"),
                    RequestFinishedEvent(
                        at = log.timestamp(2_811.milliseconds),
                        startedAt = log.timestamp(557.milliseconds),
                        groups = emptyList(),
                        request = "Session",
                        isOk = true,
                        message = ""
                    ),
                    UserFinishedEvent(at = log.timestamp(2_812.milliseconds), scenario = "Scenario"),
                    RequestFinishedEvent(
                        at = log.timestamp(2_821.milliseconds),
                        startedAt = log.timestamp(557.milliseconds),
                        groups = emptyList(),
                        request = "Session",
                        isOk = true,
                        message = ""
                    ),
                    UserFinishedEvent(at = log.timestamp(2_821.milliseconds), scenario = "Scenario"),
                    RequestFinishedEvent(
                        at = log.timestamp(2_824.milliseconds),
                        startedAt = log.timestamp(557.milliseconds),
                        groups = emptyList(),
                        request = "Session",
                        isOk = true,
                        message = ""
                    ),
                    RequestFinishedEvent(
                        at = log.timestamp(2_824.milliseconds),
                        startedAt = log.timestamp(558.milliseconds),
                        groups = emptyList(),
                        request = "Session",
                        isOk = true,
                        message = ""
                    ),
                    UserFinishedEvent(at = log.timestamp(2_825.milliseconds), scenario = "Scenario"),
                    UserFinishedEvent(at = log.timestamp(2_825.milliseconds), scenario = "Scenario"),
                    RequestFinishedEvent(
                        at = log.timestamp(2_830.milliseconds),
                        startedAt = log.timestamp(558.milliseconds),
                        groups = emptyList(),
                        request = "Session",
                        isOk = true,
                        message = ""
                    ),
                    UserFinishedEvent(at = log.timestamp(2_831.milliseconds), scenario = "Scenario"),
                    RequestFinishedEvent(
                        at = log.timestamp(2_832.milliseconds),
                        startedAt = log.timestamp(558.milliseconds),
                        groups = emptyList(),
                        request = "Session",
                        isOk = true,
                        message = ""
                    ),
                    UserFinishedEvent(at = log.timestamp(2_833.milliseconds), scenario = "Scenario"),
                    RequestFinishedEvent(
                        at = log.timestamp(2_850.milliseconds),
                        startedAt = log.timestamp(557.milliseconds),
                        groups = emptyList(),
                        request = "Session",
                        isOk = true,
                        message = ""
                    ),
                    UserFinishedEvent(at = log.timestamp(2_851.milliseconds), scenario = "Scenario")
                )

            }
        }
    }
})