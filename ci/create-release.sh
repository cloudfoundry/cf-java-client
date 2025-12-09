#!/usr/bin/env bash

set -euo pipefail

RELEASE=${1:-}
SNAPSHOT=${2:-}

function print_usage {
  echo "
Usage:

  create-release.sh <RELEASE_VERSION> <NEXT_SNAPSHOT>

Where:
  - RELEASE_VERSION like 5.15.0.RELEASE
  - NEXT_SNAPSHOT like 5.16.0.BUILD-SNAPSHOT
"
}


if [[ "$RELEASE" == v* || "$RELEASE" != *.RELEASE ]]; then
  print_usage
  exit 1
elif [[ "$SNAPSHOT" == v* || "$SNAPSHOT" != *.BUILD-SNAPSHOT ]]; then
  print_usage
  exit 1
fi

git switch -c "release-$RELEASE"

./mvnw versions:set -DnewVersion=$RELEASE -DgenerateBackupPoms=false
git add .
git commit --message "v$RELEASE Release"
git tag v$RELEASE -m "v$RELEASE"

git reset --hard HEAD^1
./mvnw versions:set -DnewVersion=$SNAPSHOT -DgenerateBackupPoms=false
git add .
git commit --message "v$SNAPSHOT Development"
