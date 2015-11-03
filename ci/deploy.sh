#!/usr/bin/env bash

set -e -x

pushd cf-java-client
  ./mvnw -Dmaven.test.skip=true deploy
popd
