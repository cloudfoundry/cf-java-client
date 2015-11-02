#!/usr/bin/env bash

set -e -x

pushd cf-java-client
  ./mvnw -P integration-test package
popd
