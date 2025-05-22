rootProject.name = "gatling-simulation-log-tools"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(
    ":parser",
    ":test-project"
)