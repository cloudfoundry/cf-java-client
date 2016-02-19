#!/usr/bin/env bash

set -e

pushd cf-java-client
  ./mvnw -q -Dmaven.test.skip=true deploy
popd
