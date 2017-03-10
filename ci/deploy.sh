#!/usr/bin/env sh

set -e

cd cf-java-client
./mvnw -q -Dmaven.test.skip=true deploy
cp target/build-info.json ../build-info/build-info.json
