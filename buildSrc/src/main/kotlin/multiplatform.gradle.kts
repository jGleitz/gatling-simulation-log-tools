plugins {
    id("gslt-module")
    kotlin("multiplatform")
    id("io.kotest.multiplatform")
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

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonTest {
            dependencies {
                implementation("io.kotest:kotest-framework-engine:5.9.1")
            }
        }

        val nonNativeTest by creating {
            dependsOn(commonTest.get())

            dependencies {
                implementation("ch.tutteli.atrium:atrium-fluent:1.2.0")
            }
        }

        jvmTest {
            dependsOn(nonNativeTest)
            dependencies {
                implementation("io.kotest:kotest-runner-junit5:5.9.1")
            }
        }

        jsTest {
            dependsOn(nonNativeTest)
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}