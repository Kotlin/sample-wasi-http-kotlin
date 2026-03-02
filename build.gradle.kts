@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform") version "2.4.255-SNAPSHOT"
}

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    jvm{
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.add("-opt-in=kotlin.ExperimentalStdlibApi")
                }
            }
        }
    }

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.addAll(
                        "-Xwasm-generate-wat",
                        "-Xwasm-enable-component-model",
                        "-opt-in=kotlin.wasm.unsafe.UnsafeWasmMemoryApi",
                        "-opt-in=kotlin.ExperimentalStdlibApi",
                        "-opt-in=kotlinx.cinterop.ExperimentalForeignApi"
                    )
                }
            }
        }
    }

    wasmWasi {
        binaries.executable()
        nodejs()
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
