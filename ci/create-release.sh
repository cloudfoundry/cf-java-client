#!/usr/bin/env sh

set -e

RELEASE=$1
SNAPSHOT=$2

PROJECTS=" \
  cloudfoundry-client \
  cloudfoundry-client-reactor \
  cloudfoundry-operations \
  cloudfoundry-util"

pushd cf-java-client
  ./mvnw versions:set -DnewVersion=$RELEASE -DgenerateBackupPoms=false
  git add .
  git commit --message "v$RELEASE Release"
  git tag -s v$RELEASE -m "v$RELEASE"

  ./mvnw -q -Dmaven.test.skip=true package
  VERSION=$(./mvnw help:evaluate -Dexpression=project.version | grep -v '\[' | grep -v 'Download')
  for PROJECT in $PROJECTS ; do
    SOURCE=$PROJECT/target/apidocs
    TARGET=../cf-java-client-documentation/api/$VERSION/$PROJECT

    echo Copying $SOURCE to $TARGET

    mkdir -p $TARGET
    rm -rf $TARGET/*
    cp -r $SOURCE/* $TARGET
  done
popd

pushd cf-java-client-documentation
  git checkout --orphan release
  git add .
  git commit --message "$VERSION Documentation Update"
popd

pushd cf-java-client
  git reset --hard HEAD^1
  ./mvnw versions:set -DnewVersion=$SNAPSHOT -DgenerateBackupPoms=false
  git add .
  git commit --message "v$SNAPSHOT Development"
popd
