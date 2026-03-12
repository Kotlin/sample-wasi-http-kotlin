BUILD_DEV_OUT_DIR=build/compileSync/wasmWasi/main/developmentExecutable/kotlin
BUILD_PROD_OUT_DIR=build/compileSync/wasmWasi/main/productionExecutable/optimized
BUILD_ROOT_DIR=build/
BINDINGS_OUT_DIR=src/wasmWasiMain/kotlin/bindings/
TOOLS_DIR=tools
WIT_BINDGEN_BRANCH=kotlin
WIT_BINDGEN_SOURCES=""
WIT_BINDGEN_PATH=$(TOOLS_DIR)/bin/wit-bindgen
PROJECT_NAME=sample-wasi-http-kotlin

.PHONY: componentify componentify-dev componentify-prod run run-dev run-prod compile compile-dev compile-prod setup setup-and-run clean install-wit-bindgen install-wit-bindgen-from-path run-wit-bindgen

# default target for when you don't want to think about it
setup-and-run: # no dependencies, as setup and run "look" independent to the Makefile, this guarantees the order:
	$(MAKE) setup
	$(MAKE) run

run: run-prod

# bit faster for re-runs
run-dev: run-wit-bindgen compile-dev componentify-dev
	wasmtime serve -S cli -W gc -W exceptions -W function-references $(BUILD_DEV_OUT_DIR)/$(PROJECT_NAME)-component.wasm

run-prod: run-wit-bindgen compile-prod componentify-prod
	wasmtime serve -S cli -W gc -W exceptions -W function-references $(BUILD_PROD_OUT_DIR)/$(PROJECT_NAME)-component.wasm

setup: install-wit-bindgen

compile: compile-prod

compile-dev: run-wit-bindgen
	./gradlew compileDevelopmentExecutableKotlinWasmWasi

compile-prod: run-wit-bindgen
	./gradlew compileProductionExecutableKotlinWasmWasiOptimize

componentify: componentify-prod

componentify-dev: compile
	wasm-tools component embed wit $(BUILD_DEV_OUT_DIR)/$(PROJECT_NAME).wasm -o $(BUILD_DEV_OUT_DIR)/$(PROJECT_NAME)-embedded.wasm
	wasm-tools component new $(BUILD_DEV_OUT_DIR)/$(PROJECT_NAME)-embedded.wasm --adapt wasi_snapshot_preview1=wasi_snapshot_preview1.reactor.wasm -o $(BUILD_DEV_OUT_DIR)/$(PROJECT_NAME)-component.wasm
	cp $(BUILD_DEV_OUT_DIR)/$(PROJECT_NAME)-component.wasm $(BUILD_ROOT_DIR)

componentify-prod: compile
	wasm-tools component embed wit $(BUILD_PROD_OUT_DIR)/$(PROJECT_NAME).wasm -o $(BUILD_PROD_OUT_DIR)/$(PROJECT_NAME)-embedded.wasm
	wasm-tools component new $(BUILD_PROD_OUT_DIR)/$(PROJECT_NAME)-embedded.wasm --adapt wasi_snapshot_preview1=wasi_snapshot_preview1.reactor.wasm -o $(BUILD_PROD_OUT_DIR)/$(PROJECT_NAME)-component.wasm
	cp $(BUILD_PROD_OUT_DIR)/$(PROJECT_NAME)-component.wasm $(BUILD_ROOT_DIR)

clean:
	./gradlew clean
	rm -rf build
	rm -rf $(TOOLS_DIR)
	rm -f $(BINDINGS_OUT_DIR)/*

# doesn't depend on install-wit-bindgen* so that we can: 
# - manually control where wit-bindgen is installed from
# - run the target multiple times with the same wit-bindgen installation
run-wit-bindgen:
	$(WIT_BINDGEN_PATH) kotlin --kotlin-imports 'impl.*' wit --out-dir=$(BINDINGS_OUT_DIR)

install-wit-bindgen:
	cargo install wit-bindgen-cli --git https://github.com/Kotlin/wit-bindgen --branch $(WIT_BINDGEN_BRANCH) --root $(TOOLS_DIR)

# using the debug build right now to make use of assertions in the unfinished state of Kotlin/wit-bindgen
install-wit-bindgen-from-path:
	cargo install wit-bindgen-cli --path $(WIT_BINDGEN_SOURCES) --root $(TOOLS_DIR) --debug --force
