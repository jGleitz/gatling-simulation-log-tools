import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask

plugins {
    id("multiplatform")
    id("com.strumenta.antlr-kotlin") version "1.0.4-SNAPSHOT"
}


val generateParserSource by tasks.registering(AntlrKotlinTask::class) {
    group = "antlr"
    source = fileTree(layout.projectDirectory.dir("src/commonMain/antlr")) {
        include("**/*.g4")
    }
    packageName = "de.joshuagleitze.gatling.simulationlog.parser"
    arguments = listOf()
    outputDirectory =
        layout.buildDirectory.dir("generated-sources/antlr/" + packageName!!.replace('.', '/')).get().asFile
}

kotlin {
    sourceSets {
        commonMain {
            kotlin {
                srcDir(generateParserSource.map { it.outputDirectory!! })
            }
            dependencies {
                implementation("com.strumenta:antlr-kotlin-runtime:1.0.3")
                implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.7.0")
            }
        }
    }
}
