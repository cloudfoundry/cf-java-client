#!/usr/bin/env bash

set -e

RELEASE=$1
SNAPSHOT=$2

mvn versions:set -DnewVersion=$RELEASE -DgenerateBackupPoms=false
git add .
git commit --message "v$RELEASE Release"

git tag v$RELEASE
git reset --hard HEAD^1

mvn versions:set -DnewVersion=$SNAPSHOT -DgenerateBackupPoms=false
git add .
git commit --message "v$SNAPSHOT Development"
