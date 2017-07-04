#!/usr/bin/env sh

set -e -u

ln -fs $PWD/maven $HOME/.m2

cd cf-java-client
./mvnw -q -P integration-test test
