#!/usr/bin/env bash

set -e

pushd cf-java-client
  ./mvnw -q -P integration-test test
popd
