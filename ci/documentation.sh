#!/usr/bin/env bash

set -e

PROJECTS=" \
  cloudfoundry-client \
  cloudfoundry-client-spring \
  cloudfoundry-operations \
  cloudfoundry-util"

pushd cf-java-client
  ./mvnw -q javadoc:javadoc
  VERSION=$(./mvnw help:evaluate -Dexpression=project.version | grep -v '\[' | grep -v 'Downloaded')
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

pushd cf-java-client-documentation
  git config --local user.name "Spring Buildmaster"
  git config --local user.email "buildmaster@springframework.org"
  git commit -m "$VERSION Documentation Update"
popd
