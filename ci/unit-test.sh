#!/usr/bin/env bash

set -e

pushd cf-java-client
  ./mvnw package
popd
