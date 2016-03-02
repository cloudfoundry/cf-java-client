#!/usr/bin/env bash

set -e

CLIENT_LOGGING_LEVEL="DEBUG"
TEST_LOGGING_LEVEL="DEBUG"

pushd cf-java-client
  ./mvnw -q -P integration-test test
popd
