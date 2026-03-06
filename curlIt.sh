#!/usr/bin/env bash

# adapted from github.com/jprendes/hyperlight-wasm-http-example

url=${1:-"http://localhost:8080"}

set -x

curl "$url"
echo ""
curl -w'\n' -d "hola mundo" "$url/echo"
curl -I -H "x-language: spanish" "$url/echo-headers"
curl -w'\n' "$url/idontexist"

