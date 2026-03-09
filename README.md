TODO before making public:
- [ ] Proper licensing of sample-wasi-http-rust

# Sample: `wasi:http` in Kotlin

This example demonstrates a super early prototype version of component model support in Kotlin. In particular, the structure of the bindings is subject to change, and certainly not final.

# Building
## Requirements
Requires `make`, `git`, and sufficiently new `cargo` and `wasm-tools`.

To run the final component, sufficiently new `wasmtime` is required.

<details>
<summary>Confirmed working versions.</summary>

```shell
cargo --version
# cargo 1.93.1 (083ac5135 2025-12-15)

wasm-tools --version
# wasm-tools 1.244.0 (d4e317f22 2026-01-06)

wasmtime --version
# wasmtime 41.0.1 (c30fce86b 2026-01-26)
```

</details>

## Non-requirements

A specific Kotlin compiler version is already selected in the Gradle config, and does not need to be installed manually.

Neither `wit-bindgen` nor `wit-deps`/`wkg` are required to be pre-installed, as wit dependencies are already fully resolved, and the required wit-bindgen fork is cloned and built manually.

## `make` command layout
### Simplest "do everything and run immediately":
```
make # or make setup-and-run
```

### More granular:
```
make setup
```
is always required in order to clone the aforementioned `wit-bindgen` fork and build it.

Then
```
make run
```
compiles and runs, while
```
make compile
```
only compiles.

