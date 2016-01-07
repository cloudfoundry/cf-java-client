#!/usr/bin/env bash

set -e

DOMAIN=$(cat connection/domain)
export TEST_HOST=api.$DOMAIN

pushd cf-java-client
  ./mvnw -P integration-test test
popd
