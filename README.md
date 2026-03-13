# Sample: `wasi:http` in Kotlin

This example demonstrates an early prototype of component model support in Kotlin.
In particular, the structure of the bindings is subject to change and is certainly not final.

## Building

### Requirements
This project requires `make`, `git`, and sufficiently recent versions of a JDK, `cargo` and `wasm-tools`.

To run the final component, a sufficiently recent version of `wasmtime` is also required.

<details>
<summary>Confirmed working versions:</summary>

```shell
cargo --version
# cargo 1.93.1 (083ac5135 2025-12-15)

wasm-tools --version
# wasm-tools 1.244.0 (d4e317f22 2026-01-06)

wasmtime --version
# wasmtime 41.0.1 (c30fce86b 2026-01-26)

javac --version
# javac 21.0.10
# but most likely, JDK 17 should be sufficient.
```

</details>

### Non-requirements

A specific Kotlin compiler version is already selected in the Gradle config and does not need to be installed manually.

Neither `wit-bindgen` nor `wit-deps`/`wkg` are required to be pre-installed, as wit dependencies are already fully resolved, and the required wit-bindgen fork is cloned and built manually.

### `make` command layout

#### Simplest "do everything and run immediately":
```shell
make # or make setup-and-run
```

#### More granular:
```shell
make setup
```
is always required in order to clone the aforementioned `wit-bindgen` fork and build it.

Then
```shell
make run
```
compiles and runs, while
```shell
make compile
```
only compiles.

#### ??
Some tasks have `dev` and `prod` variants, e.g. run, compile, by default, short versions by default use prod versions.
Dev version for fast development loop, includes incremental compilation. Prod version for the final release optimized for size and runtime performance.

#### Development and production targets

Some targets have both `-dev` (aka development) and `-prod` (aka production) variants, for example `run-dev` / `run-prod` and `compile-dev` / `compile-prod`.

When a shorter target name is available, such as `run` or `compile`, it defaults to the production variant.

Use the development variant for a faster edit-build-run loop, as it doesn't do heavy optimizations and supports incremental compilation.
Use the production variant for final builds, as it is optimized for smaller output size and better runtime performance.
