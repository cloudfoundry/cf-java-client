#!/usr/bin/env bash

set -e

source connection/domain
export TEST_HOST=$DOMAIN

pushd cf-java-client
  ./mvnw -P integration-test package
popd
