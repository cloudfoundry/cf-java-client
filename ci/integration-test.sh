#!/usr/bin/env sh

set -e

cd cf-java-client
./mvnw -q -P integration-test test
