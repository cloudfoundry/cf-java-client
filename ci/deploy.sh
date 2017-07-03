#!/usr/bin/env sh

set -e -u

cd cf-java-client
./mvnw -q -Dmaven.repo.local=../m2/repository -Dmaven.user.home=../m2 -Dmaven.test.skip=true deploy
cp target/build-info.json ../build-info/build-info.json
