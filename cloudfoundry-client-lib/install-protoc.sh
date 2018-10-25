#!/bin/sh

OS=$(uname -s | sed 's|Linux|linux|' | sed 's|Darwin|osx|')
URL="https://github.com/protocolbuffers/protobuf/releases/download/v3.0.0/protoc-3.0.0-${OS}-x86_64.zip"

curl -o protoc.zip $URL -L
unzip protoc.zip bin/protoc
rm protoc.zip

echo Please run: export PATH=$PWD/bin:$PATH
