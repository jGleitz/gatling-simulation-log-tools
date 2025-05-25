plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin.multiplatform:org.jetbrains.kotlin.multiplatform.gradle.plugin:2.1.21")
    implementation("io.kotest.multiplatform:io.kotest.multiplatform.gradle.plugin:5.9.1")
}

kotlin {
    jvmToolchain(22)
}