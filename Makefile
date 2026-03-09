BUILD_OUT_DIR=build/compileSync/wasmWasi/main/developmentExecutable/kotlin
BUILD_ROOT_DIR=build/
BINDINGS_OUT_DIR=src/wasmWasiMain/kotlin/bindings/
WIT_BINDGEN_BRANCH=kotlin
PROJECT_NAME=sample-wasi-http-kotlin

.PHONY: componentify run compile setup setup-and-run clean checkout-wit-bindgen run-wit-bindgen build-wit-bindgen

# default target for when you don't want to think about it
setup-and-run: # no dependencies, as setup and run "look" independent to the Makefile, this guarantees the order:
	$(MAKE) setup
	$(MAKE) run

# bit faster for re-runs
run: run-wit-bindgen compile componentify
	wasmtime serve -S cli -W gc -W exceptions -W function-references $(BUILD_OUT_DIR)/$(PROJECT_NAME)-component.wasm

setup: build-wit-bindgen

compile: run-wit-bindgen
	./gradlew compileDevelopmentExecutableKotlinWasmWasi

componentify: compile
	wasm-tools component embed wit $(BUILD_OUT_DIR)/$(PROJECT_NAME).wasm -o $(BUILD_OUT_DIR)/$(PROJECT_NAME)-embedded.wasm
	wasm-tools component new $(BUILD_OUT_DIR)/$(PROJECT_NAME)-embedded.wasm --adapt wasi_snapshot_preview1=wasi_snapshot_preview1.reactor.wasm -o $(BUILD_OUT_DIR)/$(PROJECT_NAME)-component.wasm
	cp $(BUILD_OUT_DIR)/$(PROJECT_NAME)-component.wasm $(BUILD_ROOT_DIR)

clean:
	./gradlew clean
	rm -rf build
	rm -rf wit-bindgen-kotlin
	rm -f $(BINDINGS_OUT_DIR)/*

# using the debug build right now to make use of assertions in the unfinished state of Kotlin/wit-bindgen
# doesn't depend on build-wit-bindgen, because git pulling is slow, and we want to be able to run this multiple times without having to wait for that
run-wit-bindgen:
	wit-bindgen-kotlin/target/debug/wit-bindgen kotlin --kotlin-imports 'impl.*' wit --out-dir=$(BINDINGS_OUT_DIR)

# could use git submodules, but they can be a bit tricky, and require an extra populate anyway, so just doing a manual clone and pull here
checkout-wit-bindgen:
	@git clone --branch $(WIT_BINDGEN_BRANCH) git@github.com:Kotlin/wit-bindgen.git wit-bindgen-kotlin 2>&1 | grep --invert-match 'fatal:.*already exists.*not.*empty directory' || { \
		actual_branch=$$(git -C wit-bindgen-kotlin branch --show-current); \
		[ "$(WIT_BINDGEN_BRANCH)" != "$$actual_branch" ] && echo -e "\e[0;31mBranch mismatch in wit-bindgen-kotlin, expected $(WIT_BINDGEN_BRANCH) but actual is $$actual_branch; Run \e[102mmake clean\e[0;31m if this was unintentional.\e[0m" && exit 1; \
		git -C wit-bindgen-kotlin pull --rebase;\
	}

build-wit-bindgen: checkout-wit-bindgen
	@# cargo -C is unstable, so cd manually to be safe
	cd wit-bindgen-kotlin && \
	RUSTFLAGS="-Awarnings" cargo build
