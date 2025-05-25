plugins {
    id("multiplatform")
}

kotlin {
    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.time.ExperimentalTime")
                optIn("kotlin.ExperimentalStdlibApi")
            }
        }
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.7.0")
                implementation("com.fleeksoft.charset:charset:0.0.4")
            }
        }
    }
}
