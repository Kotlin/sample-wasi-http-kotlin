#!/usr/bin/env bash

dir=$1

wasm-tools component embed wit build/compileSync/wasmWasi/main/developmentExecutable/kotlin/wasm-no-compose.wasm -o build/compileSync/wasmWasi/main/developmentExecutable/kotlin/wasm-no-compose-embedded.wasm

wasm-tools component new build/compileSync/wasmWasi/main/developmentExecutable/kotlin/wasm-no-compose-embedded.wasm --adapt wasi_snapshot_preview1=wasi_snapshot_preview1.reactor.wasm -o build/compileSync/wasmWasi/main/developmentExecutable/kotlin/wasm-no-compose-component.wasm