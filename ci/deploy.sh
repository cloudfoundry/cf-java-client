#!/usr/bin/env bash

set -e

pushd cf-java-client
  ./mvnw -Dmaven.test.skip=true deploy
popd
