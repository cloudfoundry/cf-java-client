#!/usr/bin/env bash

set -e

PROJECTS=" \
  cloudfoundry-client \
  cloudfoundry-client-spring \
  cloudfoundry-operations \
  cloudfoundry-util"

pushd cf-java-client
  VERSION=$(./mvnw help:evaluate -Dexpression=project.version | grep -v '\[')
  ./mvnw -q javadoc:javadoc
popd

for PROJECT in $PROJECTS ; do
  SOURCE=cf-java-client/$PROJECT/target/site/apidocs
  TARGET=cf-java-client-documentation/api/$VERSION/$PROJECT

  if [[ -e $SOURCE ]]; then
    echo Copying $SOURCE to $TARGET

    mkdir -p $TARGET
    rm -rf $TARGET/*
    cp -r $SOURCE/* $TARGET
  fi
done
