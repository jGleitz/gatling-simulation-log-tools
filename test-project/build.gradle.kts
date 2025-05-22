plugins {
    id("gslt-module")
    kotlin("jvm")
    id("io.gatling.gradle") version "3.14.3"
}

kotlin {
    jvmToolchain(23)
}