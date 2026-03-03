BUILD_OUT_DIR=build/compileSync/wasmWasi/main/developmentExecutable/kotlin

.PHONY: componentify run compile wit-bindgen setup checkout-wit-bindgen

run: wit-bindgen compile componentify
	wasmtime serve -S cli -W gc -W exceptions -W function-references $(BUILD_OUT_DIR)/wasm-no-compose-component.wasm

setup: checkout-wit-bindgen

compile: wit-bindgen
	./gradlew compileDevelopmentExecutableKotlinWasmWasi

componentify: compile
	wasm-tools component embed wit $(BUILD_OUT_DIR)/wasm-no-compose.wasm -o $(BUILD_OUT_DIR)/wasm-no-compose-embedded.wasm
	wasm-tools component new $(BUILD_OUT_DIR)/wasm-no-compose-embedded.wasm --adapt wasi_snapshot_preview1=wasi_snapshot_preview1.reactor.wasm -o $(BUILD_OUT_DIR)/wasm-no-compose-component.wasm


# using the debug build right now to make use of assertions in the unfinished state of Kotlin/wit-bindgen

wit-bindgen:
	wit-bindgen-kotlin/target/debug/wit-bindgen kotlin --kotlin-imports 'impl.*' wit --out-dir=src/wasmWasiMain/kotlin/bindings

# could use git submodules, but they can be a bit tricky, and require an extra populate anyway, so just doing a manual clone and pull here
checkout-wit-bindgen:
	git clone --branch jmrt/kotlin-old-unadapted-rebased git@github.com:Kotlin/wit-bindgen.git wit-bindgen-kotlin || git -C wit-bindgen-kotlin pull --rebase
	# cargo -C is unstable, so cd manually to be safe
	cd wit-bindgen-kotlin && \
	RUSTFLAGS="-Awarnings" cargo build
