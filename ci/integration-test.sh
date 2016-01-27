#!/usr/bin/env bash

set -e

pushd cf-java-client
  ./mvnw -P integration-test test
popd
