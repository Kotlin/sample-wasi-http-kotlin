BUILD_OUT_DIR=build/compileSync/wasmWasi/main/developmentExecutable/kotlin

.PHONY: componentify run compile wit-bindgen

run: wit-bindgen compile componentify
	wasmtime serve -S cli -W gc -W exceptions -W function-references $(BUILD_OUT_DIR)/wasm-no-compose-component.wasm

compile: wit-bindgen
	./gradlew compileDevelopmentExecutableKotlinWasmWasi

componentify: compile
	wasm-tools component embed wit $(BUILD_OUT_DIR)/wasm-no-compose.wasm -o $(BUILD_OUT_DIR)/wasm-no-compose-embedded.wasm
	wasm-tools component new $(BUILD_OUT_DIR)/wasm-no-compose-embedded.wasm --adapt wasi_snapshot_preview1=wasi_snapshot_preview1.reactor.wasm -o $(BUILD_OUT_DIR)/wasm-no-compose-component.wasm


wit-bindgen:
	../wit-bindgen-kotlin-old-unadapted-rebased/target/debug/wit-bindgen kotlin --kotlin-imports 'impl.*' wit --out-dir=src/wasmWasiMain/kotlin/bindings
