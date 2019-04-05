#!/usr/bin/env bash

set -euo pipefail

[[ -d $PWD/maven && ! -d $HOME/.m2 ]] && ln -s $PWD/maven $HOME/.m2

cd cf-java-client
./mvnw -q -Dmaven.test.skip=true deploy
cp target/build-info.json ../build-info/build-info.json
