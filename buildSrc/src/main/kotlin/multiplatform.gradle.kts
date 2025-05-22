plugins {
    id("gslt-module")
    kotlin("multiplatform")
}

kotlin {
    jvm()
    jvmToolchain(23)
    js().nodejs()
    macosX64()
    macosArm64()
    linuxX64()
    linuxArm64()
    mingwX64()
}