@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform") version "2.4.0-Beta2"
}

repositories {
    mavenCentral()
}

kotlin {
    wasmWasi {
        binaries.executable()
        nodejs()
    }
}
