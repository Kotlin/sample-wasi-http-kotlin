#!/usr/bin/env bash

compileSyncDir=build/compileSync/wasmWasi/main/developmentExecutable/kotlin

./gradlew compileDevelopmentExecutableKotlinWasmWasi
./componentify-output.sh $compileSyncDir

wasmtime serve -S cli -W gc -W exceptions -W function-references $compileSyncDir/wasm-no-compose-component.wasm
