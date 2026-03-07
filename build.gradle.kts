@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform") version "2.4.0-dev-5743"
}

repositories {
    mavenCentral()
    maven("https://packages.jetbrains.team/maven/p/kt/dev")
    // mavenLocal()
}

kotlin {
    wasmWasi {
        binaries.executable()
        nodejs()

        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.addAll(
                        "-Xwasm-generate-wat",
                        "-opt-in=kotlin.wasm.unsafe.UnsafeWasmMemoryApi",
                        "-opt-in=kotlin.ExperimentalStdlibApi",
                        "-opt-in=ComponentModelInternalApi"
                    )
                }
            }
        }
    }

    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xwasm-generate-wat",
            "-opt-in=kotlin.wasm.unsafe.UnsafeWasmMemoryApi",
            "-opt-in=kotlin.ExperimentalStdlibApi",
        )
    }

    // Common source set configuration
    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
    }
}

group = "org.example"
version = "1.0-SNAPSHOT"
