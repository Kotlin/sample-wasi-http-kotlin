@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform") version "2.4.0-dev-5756"
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
    }
}
