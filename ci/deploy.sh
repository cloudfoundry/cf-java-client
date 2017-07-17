#!/usr/bin/env sh

set -e -u

[[ -d $PWD/maven && ! -d $HOME/.m2 ]] && ln -s $PWD/maven $HOME/.m2

cd cf-java-client
./mvnw -U -q -Dmaven.test.skip=true deploy  # TODO: Remove -U
cp target/build-info.json ../build-info/build-info.json
