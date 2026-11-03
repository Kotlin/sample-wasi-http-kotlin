[![Incubator](https://jb.gg/badges/incubator-plastic.svg)](https://github.com/JetBrains#jetbrains-on-github)

# Kotlin/Wasm `wasi:http` server: a WebAssembly Component Model example

This example builds a `wasi:http` server as a WebAssembly component, using prototype Component Model support in Kotlin.
The structure of the generated bindings is subject to change and is not final.

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

Neither `wit-bindgen` nor `wit-deps`/`wkg` is required to be pre-installed, as wit dependencies are already fully resolved, and the required wit-bindgen fork is cloned and built manually.

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

#### Development and production targets

Some targets have both `-dev` (aka development) and `-prod` (aka production) variants, for example `run-dev` / `run-prod` and `compile-dev` / `compile-prod`.

When a shorter target name is available, such as `run` or `compile`, it defaults to the production variant.

Use the development variant for a faster edit-build-run loop, as it doesn't do heavy optimizations and supports incremental compilation.
Use the production variant for final builds, as it is optimized for smaller output size and better runtime performance.

## Feedback and questions:

Give it a try and share your feedback or questions in our [#webassembly](https://slack-chats.kotlinlang.org/c/webassembly)
Slack channel. [Get a Slack invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up). 

## Learn more
- [Get started with Kotlin/Wasm and WASI](https://kotlinlang.org/docs/wasm-wasi.html)